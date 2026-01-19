package sms.client.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.client.dto.destinataire.NumeroDestinataireResponseDTO;
import sms.client.entity.Solde;
import sms.client.security.JwtUtilsClient;

@Service
public class VirementService {

    private static final Logger logger = LoggerFactory.getLogger(VirementService.class);

    private final RetraitService retraitService;
    private final SoldeService soldeService;
    private final NumeroDestinataireService numeroDestinataireService;
    private final JwtUtilsClient jwtUtilsClient;

    public VirementService(
            RetraitService retraitService,
            SoldeService soldeService,
            NumeroDestinataireService numeroDestinataireService,
            JwtUtilsClient jwtUtilsClient
    ) {
        this.retraitService = retraitService;
        this.soldeService = soldeService;
        this.numeroDestinataireService = numeroDestinataireService;
        this.jwtUtilsClient = jwtUtilsClient;
    }

    /**
     * EFFECTUER UN VIREMENT
     * @param token JWT token de l'expéditeur
     * @param montant Montant à virer
     * @param numeroRecepteur Numéro du destinataire
     * @param methodName Nom de la méthode appelante (optionnel)
     * @return Solde de l'expéditeur après le virement
     */
    @Transactional
    public Solde effectuerVirement(String token, BigDecimal montant, String numeroRecepteur, String methodName) {
        
        logger.info("===========================================");
        logger.info("DÉBUT DU VIREMENT");
        if (methodName != null) {
            logger.info("Méthode appelante : {}", methodName);
        }
        logger.info("===========================================");
        
        // 1. Valider le montant
        if (montant == null || montant.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Montant invalide : {}", montant);
            throw new IllegalArgumentException("Le montant doit être supérieur à 0");
        }
        logger.info("Montant à virer : {}", montant);
        
        // 2. Récupérer l'ID de l'expéditeur
        Long expediteurId = jwtUtilsClient.getUserIdFromToken(token);
        logger.info("ID Expéditeur : {}", expediteurId);
        
        // 3. Valider le numéro récepteur
        if (numeroRecepteur == null || numeroRecepteur.trim().isEmpty()) {
            logger.error("Numéro récepteur vide ou null");
            throw new IllegalArgumentException("Le numéro du récepteur est obligatoire");
        }
        logger.info("Numéro récepteur : {}", numeroRecepteur);
        
        // 4. Récupérer tous les numéros destinataires
        logger.info("Récupération de la liste des numéros destinataires...");
        List<NumeroDestinataireResponseDTO> tousNumeros = numeroDestinataireService.getAllNumeros(token);
        
        if (tousNumeros == null || tousNumeros.isEmpty()) {
            logger.error("Aucun numéro destinataire trouvé dans la base");
            throw new RuntimeException("Aucun numéro destinataire disponible");
        }
        logger.info("Nombre de numéros trouvés : {}", tousNumeros.size());
        
        // 5. Rechercher le numéro du destinataire
        NumeroDestinataireResponseDTO numeroTrouve = tousNumeros.stream()
                .filter(n -> n.getValeur() != null && n.getValeur().equals(numeroRecepteur))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Numéro destinataire non trouvé : {}", numeroRecepteur);
                    return new RuntimeException("Le numéro destinataire " + numeroRecepteur + " n'existe pas dans le système");
                });
        
        logger.info("Numéro destinataire trouvé : ID={}, Valeur={}", numeroTrouve.getIdNumero(), numeroTrouve.getValeur());
        
        // 6. Vérifier que le numéro est associé à un utilisateur
        if (numeroTrouve.getIdUser() == null) {
            logger.error("Aucun utilisateur associé au numéro : {}", numeroRecepteur);
            throw new RuntimeException("Le numéro " + numeroRecepteur + " n'est associé à aucun utilisateur");
        }
        
        Long destinataireId = numeroTrouve.getIdUser().longValue();
        logger.info("ID Destinataire : {}", destinataireId);
        
        // 7. Vérifier que l'expéditeur n'envoie pas à lui-même
        if (expediteurId.equals(destinataireId)) {
            logger.error("Tentative de virement vers soi-même : userId={}", expediteurId);
            throw new RuntimeException("Vous ne pouvez pas effectuer un virement vers votre propre compte");
        }
        
        // 8. Débiter le compte de l'expéditeur
        logger.info("Débit du compte expéditeur (ID: {}) de {} ...", expediteurId, montant);
        Solde soldeExpediteur = retraitService.retirerSolde(token, montant, methodName != null ? methodName : "effectuerVirement");
        logger.info("Nouveau solde expéditeur : {}", soldeExpediteur.getMontant());
        
        // 9. Créditer le compte du destinataire
        logger.info("Crédit du compte destinataire (ID: {}) de {} ...", destinataireId, montant);
        Solde soldeDestinataire = soldeService.addSoldeByUserId(destinataireId, montant);
        logger.info("Nouveau solde destinataire : {}", soldeDestinataire.getMontant());
        
        // 10. Résumé de l'opération
        logger.info("===========================================");
        logger.info("VIREMENT EFFECTUÉ AVEC SUCCÈS");
        logger.info("Expéditeur (ID: {}) - Montant débité: {} - Nouveau solde: {}", 
                    expediteurId, montant, soldeExpediteur.getMontant());
        logger.info("Destinataire (ID: {}) - Montant crédité: {} - Nouveau solde: {}", 
                    destinataireId, montant, soldeDestinataire.getMontant());
        logger.info("===========================================");
        
        return soldeExpediteur;
    }
    
    /**
     * Surcharge pour appeler sans methodName
     */
    @Transactional
    public Solde effectuerVirement(String token, BigDecimal montant, String numeroRecepteur) {
        return effectuerVirement(token, montant, numeroRecepteur, null);
    }

    /**
     * OBTENIR L'ID UTILISATEUR PAR NUMÉRO
     * @param token JWT token
     * @param numero Numéro à rechercher
     * @return ID de l'utilisateur associé au numéro
     */
    public Long getUserIdByNumero(String token, String numero) {
        
        logger.info("Recherche de l'utilisateur pour le numéro : {}", numero);
        
        // Valider le numéro
        if (numero == null || numero.trim().isEmpty()) {
            logger.error("Numéro vide ou null");
            throw new IllegalArgumentException("Le numéro est obligatoire");
        }
        
        // Récupérer tous les numéros
        List<NumeroDestinataireResponseDTO> tousNumeros = numeroDestinataireService.getAllNumeros(token);
        
        if (tousNumeros == null || tousNumeros.isEmpty()) {
            logger.error("Aucun numéro destinataire trouvé");
            throw new RuntimeException("Aucun numéro destinataire disponible");
        }
        
        // Rechercher le numéro
        NumeroDestinataireResponseDTO numeroTrouve = tousNumeros.stream()
                .filter(n -> n.getValeur() != null && n.getValeur().equals(numero))
                .findFirst()
                .orElseThrow(() -> {
                    logger.error("Numéro non trouvé : {}", numero);
                    return new RuntimeException("Numéro non trouvé: " + numero);
                });
        
        // Vérifier l'association utilisateur
        if (numeroTrouve.getIdUser() == null) {
            logger.error("Aucun utilisateur associé au numéro : {}", numero);
            throw new RuntimeException("Aucun utilisateur associé au numéro: " + numero);
        }
        
        Long userId = numeroTrouve.getIdUser().longValue();
        logger.info("Utilisateur trouvé : ID={} pour le numéro {}", userId, numero);
        
        return userId;
    }
}

