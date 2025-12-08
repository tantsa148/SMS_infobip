package sms.back_end.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {

    // Déplacez les valeurs dans application.properties
    @Value("${jwt.secret:uneCleSuperSecreteTresLonguePourJWT1234567890}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms:86400000}") // 24h par défaut
    private long jwtExpirationMs;

    @Value("${jwt.refresh-expiration-ms:604800000}") // 7 jours par défaut
    private long jwtRefreshExpirationMs;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // Générer le token avec username et id
    public String generateToken(Long userId, String username) {
        return generateToken(userId, username, new HashMap<>());
    }

    // Générer le token avec claims supplémentaires
    public String generateToken(Long userId, String username, Map<String, Object> extraClaims) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", userId);
        claims.putAll(extraClaims);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Générer un refresh token
    public String generateRefreshToken(Long userId, String username) {
        return Jwts.builder()
                .setSubject(username)
                .claim("id", userId)
                .claim("type", "refresh") // Marquer comme refresh token
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtRefreshExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Récupérer le username depuis le token
    public String getUsernameFromJwt(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Récupérer l'ID depuis le token
    public Long getUserIdFromJwt(String token) {
        return extractClaim(token, claims -> {
            Object id = claims.get("id");
            if (id instanceof Number) {
                return ((Number) id).longValue();
            }
            return null;
        });
    }

    // Récupérer la date d'expiration
    public Date getExpirationDateFromToken(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Récupérer le timestamp d'expiration
    public Long getExpiryTimestamp(String token) {
        Date expiryDate = getExpirationDateFromToken(token);
        return expiryDate != null ? expiryDate.getTime() : null;
    }

    // Vérifier si le token est expiré
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

    // Vérifier le type de token
    public String getTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    // Vérifier si c'est un refresh token
    public boolean isRefreshToken(String token) {
        String type = getTokenType(token);
        return "refresh".equals(type);
    }

    // Vérifier si c'est un access token
    public boolean isAccessToken(String token) {
        String type = getTokenType(token);
        return type == null || "access".equals(type);
    }

    // Extraire toutes les claims
    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            // Retourner les claims même si expiré (pour la blacklist)
            return e.getClaims();
        }
    }

    // Méthode générique pour extraire une claim
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // Validation du token (version améliorée avec messages d'erreur)
    public boolean validateJwt(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SignatureException e) {
            System.err.println("Signature JWT invalide: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("Token JWT mal formé: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.err.println("Token JWT expiré: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("Token JWT non supporté: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Claims JWT vides: " + e.getMessage());
        }
        return false;
    }

    // Validation détaillée avec raison
    public JwtValidationResult validateJwtDetailed(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            
            return new JwtValidationResult(true, "Token valide", null);
            
        } catch (SignatureException e) {
            return new JwtValidationResult(false, "Signature invalide", "INVALID_SIGNATURE");
        } catch (MalformedJwtException e) {
            return new JwtValidationResult(false, "Token mal formé", "MALFORMED_TOKEN");
        } catch (ExpiredJwtException e) {
            return new JwtValidationResult(false, "Token expiré", "TOKEN_EXPIRED");
        } catch (UnsupportedJwtException e) {
            return new JwtValidationResult(false, "Token non supporté", "UNSUPPORTED_TOKEN");
        } catch (IllegalArgumentException e) {
            return new JwtValidationResult(false, "Claims vides", "EMPTY_CLAIMS");
        }
    }

    // Calculer le temps restant avant expiration
    public long getTimeUntilExpiration(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            Date now = new Date();
            return expiration.getTime() - now.getTime();
        } catch (Exception e) {
            return -1;
        }
    }

    // Formater le temps restant de manière lisible
    public String getTimeUntilExpirationFormatted(String token) {
        long millis = getTimeUntilExpiration(token);
        if (millis <= 0) return "Expiré";
        
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        
        if (days > 0) return days + " jours";
        if (hours > 0) return hours + " heures";
        if (minutes > 0) return minutes + " minutes";
        return seconds + " secondes";
    }

    // Vérifier si le token expire bientôt (dans les 5 minutes)
    public boolean isTokenExpiringSoon(String token, int thresholdMinutes) {
        long timeUntilExpiration = getTimeUntilExpiration(token);
        return timeUntilExpiration > 0 && timeUntilExpiration < (thresholdMinutes * 60 * 1000);
    }

    // Créer un nouveau token à partir d'un refresh token
    public String refreshAccessToken(String refreshToken) {
        if (!validateJwt(refreshToken) || !isRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Refresh token invalide");
        }
        
        Long userId = getUserIdFromJwt(refreshToken);
        String username = getUsernameFromJwt(refreshToken);
        
        return generateToken(userId, username);
    }

    // Méthodes pour les rôles/permissions (si vous les utilisez)
    public String[] getRolesFromToken(String token) {
        return extractClaim(token, claims -> {
            Object roles = claims.get("roles");
            if (roles instanceof String[]) {
                return (String[]) roles;
            }
            return new String[0];
        });
    }

    public boolean hasRole(String token, String role) {
        String[] roles = getRolesFromToken(token);
        for (String r : roles) {
            if (r.equals(role)) {
                return true;
            }
        }
        return false;
    }

    // Vérifier l'émetteur (issuer) si nécessaire
    public String getIssuerFromToken(String token) {
        return extractClaim(token, Claims::getIssuer);
    }

    // Vérifier l'audience si nécessaire
    public String getAudienceFromToken(String token) {
        return extractClaim(token, Claims::getAudience);
    }

    // Classe pour les résultats de validation détaillés
    public static class JwtValidationResult {
        private final boolean valid;
        private final String message;
        private final String errorCode;
        
        public JwtValidationResult(boolean valid, String message, String errorCode) {
            this.valid = valid;
            this.message = message;
            this.errorCode = errorCode;
        }
        
        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public String getErrorCode() { return errorCode; }
        
        @Override
        public String toString() {
            return "JwtValidationResult{" +
                    "valid=" + valid +
                    ", message='" + message + '\'' +
                    ", errorCode='" + errorCode + '\'' +
                    '}';
        }
    }

    // Getter pour l'expiration (utile pour les tests)
    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }

    public long getJwtRefreshExpirationMs() {
        return jwtRefreshExpirationMs;
    }
    
    // Générer un token avec des rôles
    public String generateTokenWithRoles(Long userId, String username, String[] roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", roles);
        return generateToken(userId, username, claims);
    }
    
    // Vérifier la validité sans expérition (pour blacklist)
    public boolean validateJwtIgnoreExpiration(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            // Ignorer l'expiration
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}