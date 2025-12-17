package sms.back_end.controller;

import java.util.Date;
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

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            String token = jwtUtils.generateToken(user.getId(), user.getUsername());

            // ⚠️ Récupération du nom du rôle
            UserDTO userDTO = new UserDTO(user.getId(),user.getUsername(), user.getRole().getRole());

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

    // --- Register ---
    @PostMapping("/register")
public ResponseEntity<?> register(@RequestBody RegisterRequestDTO registerRequest) {
    try {
        User user = authService.register(
                registerRequest.getUsername(),
                registerRequest.getPassword(),
                registerRequest.getRole()
        );

        // ⚠️ Inclure l’ID généré automatiquement
        UserDTO userDTO = new UserDTO(user.getId(), user.getUsername(), user.getRole().getRole());

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

   
    // --- Current User ---
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

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "success", false,
                            "message", "Utilisateur non trouvé en base !"
                    ));
        }

        UserDTO userDTO = new UserDTO(user.getId(),user.getUsername(), user.getRole().getRole());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Utilisateur connecté récupéré",
                "user", userDTO
        ));
    }

    // --- Logout ---
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        String token = extractToken(request);
        
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "No token provided", "success", false)
            );
        }
        
        if (!jwtUtils.validateJwt(token)) {
            return ResponseEntity.badRequest().body(
                Map.of("error", "Invalid token", "success", false)
            );
        }
        
        try {
            Long expiryTime = jwtUtils.getExpiryTimestamp(token);
            tokenBlacklistService.blacklistToken(token, expiryTime);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Logout successful");
            response.put("timestamp", new Date());
            response.put("tokenRevoked", true);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                Map.of("error", "Logout failed: " + e.getMessage(), "success", false)
            );
        }
    }

    // --- Logout All (simple) ---
    @PostMapping("/logout-all")
    public ResponseEntity<?> logoutAllDevices(HttpServletRequest request) {
        return logout(request);
    }

    // --- Validate Token ---
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(HttpServletRequest request) {
        String token = extractToken(request);
        
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(
                Map.of("valid", false, "error", "No token provided")
            );
        }
        
        Map<String, Object> response = new HashMap<>();
        
        if (tokenBlacklistService.isTokenBlacklisted(token)) {
            response.put("valid", false);
            response.put("error", "Token revoked");
            response.put("revoked", true);
            return ResponseEntity.status(401).body(response);
        }
        
        boolean isValid = jwtUtils.validateJwt(token);
        response.put("valid", isValid);
        
        if (isValid) {
            response.put("username", jwtUtils.getUsernameFromJwt(token));
            Date expiry = jwtUtils.getExpirationDateFromToken(token);
            response.put("expiresAt", expiry);
            response.put("expiresIn", expiry.getTime() - System.currentTimeMillis());
        } else {
            response.put("error", "Invalid JWT");
        }
        
        return ResponseEntity.ok(response);
    }

    // --- Admin Blacklist Stats ---
    @GetMapping("/admin/blacklist-stats")
    public ResponseEntity<?> getBlacklistStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("size", tokenBlacklistService.getBlacklistSize());
        stats.put("timestamp", new Date());
        return ResponseEntity.ok(stats);
    }

    // --- Helper ---
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
