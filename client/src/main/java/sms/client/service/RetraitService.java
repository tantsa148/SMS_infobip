package sms.client.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.client.dto.destinataire.NumeroDestinataireResponseDTO;
import sms.client.dto.modele.ModeleMessageDTO;
import sms.client.dto.transaction.TransactionRequestDTO;
import sms.client.entity.Solde;
import sms.client.repository.SoldeRepository;
import sms.client.security.JwtUtilsClient;

@Service
public class RetraitService {

    private final SoldeRepository soldeRepository;
    private final JwtUtilsClient jwtUtilsClient;
    private final ModeleMessageClientService modeleMessageClientService;
    private final TransactionClientService transactionClientService;
    private final NumeroDestinataireService numeroDestinataireService;

    public RetraitService(SoldeRepository soldeRepository, JwtUtilsClient jwtUtilsClient, 
                          ModeleMessageClientService modeleMessageClientService,
                          TransactionClientService transactionClientService,
                          NumeroDestinataireService numeroDestinataireService) {
        this.soldeRepository = soldeRepository;
        this.jwtUtilsClient = jwtUtilsClient;
        this.modeleMessageClientService = modeleMessageClientService;
        this.transactionClientService = transactionClientService;
        this.numeroDestinataireService = numeroDestinataireService;
    }

    /**
     * RETIRER DU SOLDE
     * Déduit le montant spécifié du solde de l'utilisateur connecté
     * et enregistre la transaction
     */
@Transactional
public Solde retirerSolde(String token, BigDecimal montant, String methodName) {
    
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
    
    // Récupérer l'ID utilisateur depuis le token
    Long userId = jwtUtilsClient.getUserIdFromToken(token);
    
    System.out.println("User ID : " + userId);
    System.out.println("Montant à retirer : " + montant);
    
    // Récupérer le solde de l'utilisateur
    Solde solde = soldeRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Solde non trouvé pour l'utilisateur " + userId));
    
    System.out.println("Solde actuel : " + solde.getMontant());
    
    // Vérifier si le solde est suffisant
    if (solde.getMontant().compareTo(montant) < 0) {
        throw new RuntimeException("Solde insuffisant. Solde actuel: " + solde.getMontant() + ", Montant demandé: " + montant);
    }
    
    // Déduire le montant
    BigDecimal nouveauSolde = solde.getMontant().subtract(montant);
    solde.setMontant(nouveauSolde);
    
    System.out.println("Nouveau solde : " + nouveauSolde);
    System.out.println("===========================================");
    
    // Sauvegarder le solde mis à jour
    Solde soldeSauvegarde = soldeRepository.save(solde);
    
    // Créer et envoyer la transaction
    // Récupérer le premier numéro de l'utilisateur
    NumeroDestinataireResponseDTO numero = numeroDestinataireService.getFirstNumeroByUserId(userId, token);
    
    // Créer la requête de transaction
    TransactionRequestDTO transactionDTO = new TransactionRequestDTO();
    transactionDTO.setIdNumeroExpediteur(modele.getIdExpediteur()); // ID expediteur du modèle
    transactionDTO.setIdMessage(modele.getIdMessage()); // ID message du modèle
    transactionDTO.setIdNumeroDestinataire(numero != null ? numero.getIdNumero() : null); // ID numero correspondant à l'userId
    transactionDTO.setMontant(montant); // Montant du retrait
    transactionDTO.setReference("REF-RETRAIT-" + System.currentTimeMillis()); // Référence hardcodée pour le moment
    
    System.out.println("Envoi de la transaction avec référence: " + transactionDTO.getReference());
    
    // Envoyer la transaction
    transactionClientService.envoyerTransaction(transactionDTO, token);
    
    System.out.println("Transaction envoyée avec succès!");
    
    return soldeSauvegarde;
}
}

