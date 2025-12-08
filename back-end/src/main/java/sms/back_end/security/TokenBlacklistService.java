// sms.back_end.security.TokenBlacklistService
package sms.back_end.security;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class TokenBlacklistService {
    
    // Stockage en mémoire des tokens blacklistés
    private final Set<String> blacklistedTokens = ConcurrentHashMap.newKeySet();
    
    // Map pour stocker l'expiration des tokens (pour nettoyage automatique)
    private final ConcurrentMap<String, Long> tokenExpiryMap = new ConcurrentHashMap<>();
    
    /**
     * Ajouter un token à la blacklist
     * @param token Le token JWT à blacklister
     * @param expiryTime Le timestamp d'expiration du token (en millisecondes)
     */
    public void blacklistToken(String token, Long expiryTime) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        
        blacklistedTokens.add(token);
        if (expiryTime != null) {
            tokenExpiryMap.put(token, expiryTime);
        }
        
        // Log pour debug
        System.out.println("[TokenBlacklist] Token blacklisted: " + token.substring(0, Math.min(20, token.length())) + "...");
        System.out.println("[TokenBlacklist] Total blacklisted tokens: " + blacklistedTokens.size());
    }
    
    /**
     * Vérifier si un token est blacklisté
     * @param token Le token à vérifier
     * @return true si le token est blacklisté
     */
    public boolean isTokenBlacklisted(String token) {
        if (token == null) {
            return false;
        }
        
        // Nettoyer les tokens expirés avant vérification
        cleanupExpiredTokens();
        
        boolean isBlacklisted = blacklistedTokens.contains(token);
        
        if (isBlacklisted) {
            System.out.println("[TokenBlacklist] Token found in blacklist");
        }
        
        return isBlacklisted;
    }
    
    /**
     * Supprimer un token de la blacklist (pour le "unblacklist")
     * @param token Le token à retirer
     */
    public void removeFromBlacklist(String token) {
        if (token != null) {
            blacklistedTokens.remove(token);
            tokenExpiryMap.remove(token);
            System.out.println("[TokenBlacklist] Token removed from blacklist");
        }
    }
    
    /**
     * Nettoyer automatiquement les tokens expirés
     * Exécuté toutes les 30 minutes
     */
    @Scheduled(fixedRate = 1800000) // 30 minutes en millisecondes
    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        int initialSize = blacklistedTokens.size();
        
        // Supprimer les tokens expirés
        tokenExpiryMap.entrySet().removeIf(entry -> {
            if (entry.getValue() < currentTime) {
                blacklistedTokens.remove(entry.getKey());
                return true;
            }
            return false;
        });
        
        int removed = initialSize - blacklistedTokens.size();
        if (removed > 0) {
            System.out.println("[TokenBlacklist] Cleaned up " + removed + " expired tokens");
        }
    }
    
    /**
     * Obtenir le nombre de tokens blacklistés
     * @return Le nombre de tokens actuellement blacklistés
     */
    public int getBlacklistSize() {
        return blacklistedTokens.size();
    }
    
    /**
     * Vider complètement la blacklist
     * (Utile pour les tests ou en cas de problème)
     */
    public void clearBlacklist() {
        blacklistedTokens.clear();
        tokenExpiryMap.clear();
        System.out.println("[TokenBlacklist] Blacklist cleared");
    }
}