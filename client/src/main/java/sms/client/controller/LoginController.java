package sms.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import sms.client.dto.user.LoginResponseDTO;
import sms.client.service.AuthLoginService;

@Controller
public class LoginController {

    private final AuthLoginService authLoginService;

    public LoginController(AuthLoginService authLoginService) {
        this.authLoginService = authLoginService;
    }

    // 🔹 AFFICHAGE DU FORMULAIRE DE CONNEXION
    @GetMapping("/login")
    public String showLoginForm() {
        return "login"; // login.html
    }

    // 🔹 TRAITEMENT DU FORMULAIRE
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {

        LoginResponseDTO response = authLoginService.login(username, password);

        if (response != null && response.isSuccess()) {

            // 🔐 STOCKAGE DU TOKEN EN SESSION
            session.setAttribute("JWT_TOKEN", response.getToken());

            // ✅ Redirection SANS exposer le token
            return "redirect:/solde";

        } else {
            // ❌ Login échoué
            model.addAttribute(
                    "error",
                    response != null ? response.getMessage() : "Erreur de connexion"
            );
            return "login";
        }
    }
}
