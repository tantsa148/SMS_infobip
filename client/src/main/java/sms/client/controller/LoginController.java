package sms.client.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        return "login"; // nom de la vue Thymeleaf login.html
    }

    // 🔹 TRAITEMENT DU FORMULAIRE
    @PostMapping("/login")
    public String login(
            @RequestParam String username,
            @RequestParam String password,
            Model model) {

        LoginResponseDTO response = authLoginService.login(username, password);

        if (response != null && response.isSuccess()) {
            model.addAttribute("success", response.getMessage());
            model.addAttribute("token", response.getToken());
            model.addAttribute("username", response.getUser().getUsername());
            return "dashboard"; // ou autre page après connexion
        } else {
            model.addAttribute("error", response != null ? response.getMessage() : "Erreur inconnue");
            return "login";
        }
    }
}
