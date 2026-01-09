package sms.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import sms.client.dto.destinataire.NumeroDestinataireResponseDTO;
import sms.client.security.JwtUtilsClient;
import sms.client.service.NumeroDestinataireService;

@Controller
public class TestNumeroController {

    private final NumeroDestinataireService numeroService;
    private final JwtUtilsClient jwtUtilsClient; // 🔹 pour extraire userId du token

    public TestNumeroController(
            NumeroDestinataireService numeroService,
            JwtUtilsClient jwtUtilsClient
    ) {
        this.numeroService = numeroService;
        this.jwtUtilsClient = jwtUtilsClient;
    }

    @GetMapping("/test-numero")
    public String testGetFirstNumero(HttpSession session, Model model) {
        try {
            // 🔹 Récupérer le token depuis la session
            String token = (String) session.getAttribute("JWT_TOKEN");
            if (token == null) {
                model.addAttribute("message", "Aucun token trouvé. Veuillez vous connecter.");
                return "testNumero";
            }

            // 🔹 Extraire le userId depuis le JWT
            Long userId = jwtUtilsClient.getUserIdFromToken(token);

            // 🔹 Récupérer le premier numéro destinataire pour cet utilisateur
            NumeroDestinataireResponseDTO numero = numeroService.getFirstNumeroByUserId(userId, token);

            if (numero != null) {
                model.addAttribute("message", "Premier numéro trouvé : " + numero.getValeur());
                model.addAttribute("idNumero", numero.getIdNumero());
            } else {
                model.addAttribute("message", "Aucun numéro trouvé pour l'utilisateur " + userId);
            }

        } catch (Exception e) {
            model.addAttribute("message", "Erreur lors de la récupération du numéro : " + e.getMessage());
            e.printStackTrace();
        }

        return "testNumero"; // Vue Thymeleaf
    }
}
