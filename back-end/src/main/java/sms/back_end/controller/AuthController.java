package sms.back_end.controller;

import java.util.Date; // ⚠️ Changez de java.sql.Date à java.util.Date
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import sms.back_end.dto.LoginRequestDTO;
import sms.back_end.dto.RegisterRequestDTO;
import sms.back_end.dto.UserDTO;
import sms.back_end.entity.User;
import sms.back_end.repository.UserRepository;
import sms.back_end.security.JwtUtils;
import sms.back_end.security.TokenBlacklistService;
import sms.back_end.service.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    // --- Login ---
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(), loginRequest.getPassword()
                )
            );

            // Récupérer l'id depuis la DB
            User user = userRepository.findByUsername(loginRequest.getUsername()).get();

            String token = jwtUtils.generateToken(user.getId(), user.getUsername());

            UserDTO userDTO = new UserDTO(user.getUsername(), user.getRole());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Connexion réussie",
                "token", token,
                "user", userDTO
            ));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "Nom d’utilisateur ou mot de passe incorrect"
            ));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) {
        try {
            User user = authService.register(
                    registerRequest.getUsername(),
                    registerRequest.getPassword(),
                    registerRequest.getRole()
            );

            UserDTO userDTO = new UserDTO(user.getUsername(), user.getRole());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Utilisateur créé avec succès",
                    "user", userDTO
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !(auth.getPrincipal() instanceof UserDetails)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "success", false,
                            "message", "Utilisateur non connecté !"
                    ));
        }

        UserDetails userDetails = (UserDetails) auth.getPrincipal();

        // Récupérer l'utilisateur complet depuis la DB
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "Utilisateur non trouvé en base !"
                    ));
        }

        // Construire le DTO
        UserDTO userDTO = new UserDTO(user.getUsername(), user.getRole());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Utilisateur connecté récupéré",
                "user", userDTO
        ));
    }
    
    /**
     * Endpoint pour se déconnecter
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = extractToken(request);
        
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "No token provided", "success", false)
            );
        }
        
        // Valider le token d'abord
        if (!jwtUtils.validateJwt(token)) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "Invalid token", "success", false)
            );
        }
        
        try {
            // Récupérer la date d'expiration
            Long expiryTime = jwtUtils.getExpiryTimestamp(token);
            
            // Ajouter à la blacklist
            tokenBlacklistService.blacklistToken(token, expiryTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Logout successful");
            response.put("timestamp", new Date()); // ⚠️ Maintenant java.util.Date
            response.put("tokenRevoked", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                Map.of("error", "Logout failed: " + e.getMessage(), "success", false)
            );
        }
    }
    
    /**
     * Endpoint pour forcer le logout sur tous les appareils
     * (Nécessite généralement un système plus complexe)
     */
    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAllDevices(HttpServletRequest request) {
        // Cette implémentation simple blacklist juste le token courant
        // Pour une vraie solution "logout all", il faut tracker tous les tokens émis
        return logout(request);
    }
    
    /**
     * Endpoint pour vérifier l'état d'un token
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String token = extractToken(request);
        
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("valid", false, "error", "No token provided")
            );
        }
        
        Map<String, Object> response = new HashMap<>();
        
        // 1. Vérifier si blacklisté
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            response.put("valid", false);
            response.put("error", "Token revoked");
            response.put("revoked", true);
            return ResponseEntity.status(401).body(response);
        }
        
        // 2. Vérifier la validité JWT
        boolean isValid = jwtUtils.validateJwt(token);
        response.put("valid", isValid);
        
        if (isValid) {
            response.put("username", jwtUtils.getUsernameFromJwt(token));
            Date expiry = jwtUtils.getExpirationDateFromToken(token); // ⚠️ java.util.Date
            response.put("expiresAt", expiry);
            response.put("expiresIn", expiry.getTime() - System.currentTimeMillis());
        } else {
            response.put("error", "Invalid JWT");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Endpoint admin pour voir la taille de la blacklist
     */
    @GetMapping("/admin/blacklist-stats")
    public ResponseEntity<?> getBlacklistStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("size", tokenBlacklistService.getBlacklistSize());
        stats.put("timestamp", new Date()); // ⚠️ java.util.Date
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Extraire le token du header Authorization
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}