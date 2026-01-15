package sms.client.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.client.entity.Solde;
import sms.client.repository.SoldeRepository;
import sms.client.security.JwtUtilsClient;

@Service
public class SoldeService {

    private final SoldeRepository soldeRepository;
    private final JwtUtilsClient jwtUtilsClient;

    public SoldeService(SoldeRepository soldeRepository, JwtUtilsClient jwtUtilsClient) {
        this.soldeRepository = soldeRepository;
        this.jwtUtilsClient = jwtUtilsClient;
    }

    /**
     * 🔹 AJOUTER DU SOLDE
     * Ajoute le montant spécifié au solde de l'utilisateur connecté
     */
    @Transactional
    public Solde addSolde(String token, BigDecimal montant) {
        
        // 1️⃣ Récupérer l'ID utilisateur depuis le token
        Long userId = jwtUtilsClient.getUserIdFromToken(token);
        
        // 2️⃣ Récupérer ou créer le solde
        Solde solde = soldeRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Solde s = new Solde();
                    s.setUserId(userId);
                    s.setMontant(BigDecimal.ZERO);
                    return soldeRepository.save(s);
                });
        
        // 3️⃣ Ajouter le montant
        solde.setMontant(solde.getMontant().add(montant));
        
        // 4️⃣ Sauvegarder
        return soldeRepository.save(solde);
    }

    /**
     * 🔹 RÉCUPÉRER LE SOLDE
     * Retourne le solde de l'utilisateur connecté
     */
    public Solde getSolde(String token) {
        
        // 1️⃣ Récupérer l'ID utilisateur depuis le token
        Long userId = jwtUtilsClient.getUserIdFromToken(token);
        
        // 2️⃣ Récupérer le solde
        return soldeRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Solde non trouvé pour l'utilisateur " + userId));
    }

    /**
     * 🔹 RÉCUPÉRER OU CRÉER LE SOLDE
     * Retourne le solde existant ou crée un nouveau solde à zéro
     */
    public Solde getOrCreateSolde(String token) {
        
        // 1️⃣ Récupérer l'ID utilisateur depuis le token
        Long userId = jwtUtilsClient.getUserIdFromToken(token);
        
        // 2️⃣ Récupérer ou créer le solde
        return soldeRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Solde s = new Solde();
                    s.setUserId(userId);
                    s.setMontant(BigDecimal.ZERO);
                    return soldeRepository.save(s);
                });
    }

    /**
     * 🔹 AJOUTER DU SOLDE PAR USER ID
     * Ajoute le montant spécifié au solde d'un utilisateur (par son ID)
     */
    @Transactional
    public Solde addSoldeByUserId(Long userId, BigDecimal montant) {
        
        // 1️⃣ Récupérer ou créer le solde
        Solde solde = soldeRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Solde s = new Solde();
                    s.setUserId(userId);
                    s.setMontant(BigDecimal.ZERO);
                    return soldeRepository.save(s);
                });
        
        // 2️⃣ Ajouter le montant
        solde.setMontant(solde.getMontant().add(montant));
        
        // 3️⃣ Sauvegarder
        return soldeRepository.save(solde);
    }
}

