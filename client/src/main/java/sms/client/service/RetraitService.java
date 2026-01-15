package sms.client.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.client.entity.Solde;
import sms.client.repository.SoldeRepository;
import sms.client.security.JwtUtilsClient;

@Service
public class RetraitService {

    private final SoldeRepository soldeRepository;
    private final JwtUtilsClient jwtUtilsClient;

    public RetraitService(SoldeRepository soldeRepository, JwtUtilsClient jwtUtilsClient) {
        this.soldeRepository = soldeRepository;
        this.jwtUtilsClient = jwtUtilsClient;
    }

    /**
     * RETIRER DU SOLDE
     * Déduit le montant spécifié du solde de l'utilisateur connecté
     */
    @Transactional
    public Solde retirerSolde(String token, BigDecimal montant) {
        
        // Récupérer l'ID utilisateur depuis le token
        Long userId = jwtUtilsClient.getUserIdFromToken(token);
        
        // Récupérer le solde de l'utilisateur
        Solde solde = soldeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Solde non trouvé pour l'utilisateur " + userId));
        
        // Vérifier si le solde est suffisant
        if (solde.getMontant().compareTo(montant) < 0) {
            throw new RuntimeException("Solde insuffisant. Solde actuel: " + solde.getMontant() + ", Montant demandé: " + montant);
        }
        
        // Déduire le montant
        BigDecimal nouveauSolde = solde.getMontant().subtract(montant);
        solde.setMontant(nouveauSolde);
        
        // Sauvegarder
        return soldeRepository.save(solde);
    }
}

