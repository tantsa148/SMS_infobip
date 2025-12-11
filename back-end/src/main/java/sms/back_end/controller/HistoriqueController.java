package sms.back_end.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import jakarta.servlet.http.HttpServletRequest;
import sms.back_end.entity.Historique;
import sms.back_end.security.JwtUtils;
import sms.back_end.service.HistoriqueService;

@RestController
@RequestMapping("/api/historique")
public class HistoriqueController {

    private final HistoriqueService historiqueService;
    private final JwtUtils jwtUtils;

    // Constructeur pour l'injection
    public HistoriqueController(HistoriqueService historiqueService, JwtUtils jwtUtils) {
        this.historiqueService = historiqueService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Récupère la liste des SMS pour l'utilisateur connecté
     */
    @GetMapping
    public List<Historique> getHistorique(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant");
        }

        String token = authHeader.substring(7);
        Long userId = jwtUtils.getUserIdFromJwt(token);

        return historiqueService.getHistoriqueByUserId(userId);
    }

    /**
     * Récupère les détails d'un SMS depuis Infobip pour l'utilisateur connecté
     */
    @GetMapping("/{idEnvoi}")
    public JsonNode getSmsDetails(
            @PathVariable Long idEnvoi,
            HttpServletRequest request
    ) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token manquant");
        }

        String token = authHeader.substring(7);
        Long userId = jwtUtils.getUserIdFromJwt(token);

        // Récupère l'historique correspondant à cet envoi et utilisateur
        Historique historique = historiqueService.getHistoriqueByUserId(userId)
                .stream()
                .filter(h -> h.getIdEnvoi().equals(idEnvoi))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Message non trouvé"));

        // Appel à Infobip pour récupérer les détails
        return historiqueService.getSmsDetailsFromInfobip(historique);
    }
}
