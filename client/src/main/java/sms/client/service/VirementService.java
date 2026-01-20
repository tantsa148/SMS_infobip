package sms.client.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.client.dto.destinataire.NumeroDestinataireResponseDTO;
import sms.client.dto.modele.ModeleMessageDTO;
import sms.client.dto.transaction.TransactionAvecNumeroRequestDTO;
import sms.client.dto.transaction.TransactionResponseDTO;
import sms.client.entity.Solde;
import sms.client.repository.SoldeRepository;
import sms.client.security.JwtUtilsClient;

@Service
public class VirementService {

    private final SoldeRepository soldeRepository;
    private final JwtUtilsClient jwtUtilsClient;
    private final NumeroDestinataireService numeroDestinataireService;
    private final ModeleMessageClientService modeleMessageClientService;
    private final TransactionClientService transactionClientService;

    public VirementService(
            SoldeRepository soldeRepository,
            JwtUtilsClient jwtUtilsClient,
            NumeroDestinataireService numeroDestinataireService,
            ModeleMessageClientService modeleMessageClientService,
            TransactionClientService transactionClientService
    ) {
        this.soldeRepository = soldeRepository;
        this.jwtUtilsClient = jwtUtilsClient;
        this.numeroDestinataireService = numeroDestinataireService;
        this.modeleMessageClientService = modeleMessageClientService;
        this.transactionClientService = transactionClientService;
    }

    /**
     * Effectuer un virement d'un utilisateur à un autre
     * @param token JWT token de l'expéditeur
     * @param montant Montant à virer
     * @param numero Numéro du destinataire
     * @param methodName Nom de la méthode appelante (du controller)
     * @return Le solde de l'expéditeur après le virement
     */
    @Transactional
    public Solde effectuerVirement(String token, BigDecimal montant, String numero, String methodName) {
        
        System.out.println("===========================================");
        System.out.println("Méthode appelante : " + methodName);
        System.out.println("===========================================");
        
        Long userId = jwtUtilsClient.getUserIdFromToken(token);
    
        // 1. Valider le montant
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Le montant doit être supérieur à 0");
        }
        System.out.println("Montant à virer : " + montant);
        
        // 2. Récupérer le premier numéro de l'expéditeur
        NumeroDestinataireResponseDTO numeroExpediteur = numeroDestinataireService.getFirstNumeroByUserId(userId, token);
        System.out.println("ID Numero expéditeur : " + (numeroExpediteur != null ? numeroExpediteur.getIdNumero() : "null"));
        
        if (numeroExpediteur == null) {
            throw new RuntimeException("Aucun numéro expéditeur trouvé pour l'utilisateur " + userId);
        }
        
        // 3. Valider le numéro
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Le numéro du destinataire est obligatoire");
        }
        System.out.println("Numéro : " + numero);
        
        // 4. Récupérer tous les numéros destinataires
        System.out.println("Récupération de la liste des numéros destinataires...");
        List<NumeroDestinataireResponseDTO> tousNumeros = numeroDestinataireService.getAllNumeros(token);
        
        if (tousNumeros == null || tousNumeros.isEmpty()) {
            throw new RuntimeException("Aucun numéro destinataire disponible");
        }
        System.out.println("Nombre de numéros trouvés : " + tousNumeros.size());
        
        // 5. Rechercher le numéro du destinataire
        NumeroDestinataireResponseDTO numeroTrouve = tousNumeros.stream()
                .filter(n -> n.getValeur() != null && n.getValeur().equals(numero))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Le numéro " + numero + " n'existe pas dans le système"));
        
        System.out.println("Numéro destinataire trouvé : ID=" + numeroTrouve.getIdNumero() + ", Valeur=" + numeroTrouve.getValeur());
        
        // 6. Vérifier que le numéro est associé à un utilisateur
        if (numeroTrouve.getIdUser() == null) {
            throw new RuntimeException("Le numéro " + numero + " n'est associé à aucun utilisateur");
        }
        
        Long destinataireId = numeroTrouve.getIdUser().longValue();
        System.out.println("ID Destinataire : " + destinataireId);
        
        // 7. Vérifier que l'expéditeur n'envoie pas à lui-même
        if (userId.equals(destinataireId)) {
            throw new RuntimeException("Vous ne pouvez pas effectuer un virement vers votre propre compte");
        }
        
        // 8. Débiter le compte de l'expéditeur
        System.out.println("Débit du compte expéditeur (ID: " + userId + ") de " + montant + " ...");
        Solde soldeExpediteur = soldeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Solde non trouvé pour l'expéditeur " + userId));
        
        if (soldeExpediteur.getMontant().compareTo(montant) < 0) {
            throw new RuntimeException("Solde insuffisant. Solde actuel: " + soldeExpediteur.getMontant());
        }
        
        soldeExpediteur.setMontant(soldeExpediteur.getMontant().subtract(montant));
        Solde soldeExpediteurSauvegarde = soldeRepository.save(soldeExpediteur);
        System.out.println("Nouveau solde expéditeur : " + soldeExpediteurSauvegarde.getMontant());
        
        // 9. Créditer le compte du destinataire
        System.out.println("Crédit du compte destinataire (ID: " + destinataireId + ") de " + montant + " ...");
        Solde soldeDestinataire = soldeRepository.findByUserId(destinataireId)
                .orElseGet(() -> {
                    Solde s = new Solde();
                    s.setUserId(destinataireId);
                    s.setMontant(BigDecimal.ZERO);
                    return soldeRepository.save(s);
                });
        
        soldeDestinataire.setMontant(soldeDestinataire.getMontant().add(montant));
        Solde soldeDestinataireSauvegarde = soldeRepository.save(soldeDestinataire);
        System.out.println("Nouveau solde destinataire : " + soldeDestinataireSauvegarde.getMontant());
        
        // 10. Vérifier si un modèle existe pour cette méthode et envoyer la transaction SMS
        ModeleMessageDTO modele = modeleMessageClientService.findByMethode(methodName, token);
        if (modele != null) {
            System.out.println("Modèle trouvé pour la méthode : " + methodName);
            System.out.println("Envoi de la transaction SMS...");
            
            // Créer le DTO de transaction
            TransactionAvecNumeroRequestDTO transactionRequest = new TransactionAvecNumeroRequestDTO();
            transactionRequest.setIdNumeroExpediteur(modele.getIdExpediteur());
            transactionRequest.setIdNumeroDestinataire(numeroExpediteur.getIdNumero());
            transactionRequest.setIdMessage(modele.getIdMessage());
            transactionRequest.setReference("REF-VIREMENT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            transactionRequest.setMontant(montant.doubleValue());
            transactionRequest.setNumero(numero);
            
            System.out.println("Transaction Request : " + transactionRequest);
            
            try {
                // Envoyer la transaction
                TransactionResponseDTO transactionResponse = 
                    transactionClientService.envoyerTransactionAvecNumero(transactionRequest, token);
                
                System.out.println("Transaction SMS envoyée avec succès : " + transactionResponse);
            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi de la transaction SMS : " + e.getMessage());
                // On ne fait pas échouer le virement si l'envoi du SMS échoue
            }
        } else {
            System.out.println("Aucun modèle trouvé pour la méthode : " + methodName);
        }
        
        // 11. Résumé de l'opération
        System.out.println("===========================================");
        System.out.println("VIREMENT EFFECTUÉ AVEC SUCCÈS");
        System.out.println("Expéditeur (ID: " + userId + ") - Montant débité: " + montant + " - Nouveau solde: " + soldeExpediteurSauvegarde.getMontant());
        System.out.println("Destinataire (ID: " + destinataireId + ") - Montant crédité: " + montant + " - Nouveau solde: " + soldeDestinataireSauvegarde.getMontant());
        System.out.println("===========================================");
        
        return soldeExpediteurSauvegarde;
    }

    /**
     * Débiter le compte de l'utilisateur connecté (sans virement)
     * @param token JWT token de l'utilisateur
     * @param montant Montant à débiter
     * @param methodName Nom de la méthode appelante (du controller)
     * @return Le solde mis à jour
     */
    @Transactional
    public Solde debiterCompte(String token, BigDecimal montant, String methodName) {
        
        System.out.println("===========================================");
        System.out.println("Méthode appelante : " + methodName);
        System.out.println("===========================================");
        
        // Vérifier si un modèle existe pour cette méthode
        ModeleMessageDTO modele = modeleMessageClientService.findByMethode(methodName, token);
        if (modele != null) {
            System.out.println("oui");
        } else {
            System.out.println("non");
        }
        
        Long userId = jwtUtilsClient.getUserIdFromToken(token);
        System.out.println("User ID : " + userId);
        System.out.println("Montant à débiter : " + montant);
        
        Solde solde = soldeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Solde non trouvé pour l'utilisateur " + userId));
        
        System.out.println("Solde actuel : " + solde.getMontant());
        
        if (solde.getMontant().compareTo(montant) < 0) {
            throw new RuntimeException("Solde insuffisant. Solde actuel: " + solde.getMontant() + ", Montant demandé: " + montant);
        }
        
        BigDecimal nouveauSolde = solde.getMontant().subtract(montant);
        solde.setMontant(nouveauSolde);
        
        System.out.println("Nouveau solde : " + nouveauSolde);
        
        return soldeRepository.save(solde);
    }
}