package sms.client.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import sms.client.dto.plateforme.PlateformeDTO;
import sms.client.dto.user.RegisterResponseDTO;
import sms.client.service.AuthRegisterService;
import sms.client.service.PlateformeService;

@Controller
public class RegisterController {

    private final AuthRegisterService authRegisterService;
    private final PlateformeService plateformeService; // injection du service

    public RegisterController(AuthRegisterService authRegisterService,
                              PlateformeService plateformeService) {
        this.authRegisterService = authRegisterService;
        this.plateformeService = plateformeService;
    }

    // 🔹 Afficher le formulaire
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // Récupérer la liste des plateformes depuis le service
        List<PlateformeDTO> plateformes = plateformeService.getPlateformes();
        model.addAttribute("plateformes", plateformes);
        return "register"; // nom de la vue Thymeleaf
    }

    // 🔹 Traiter le formulaire
    @PostMapping("/register")
    public String register(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String valeurNumero,
            @RequestParam int plateformeId, // récupérer l'id plateforme choisi
            Model model) {

        RegisterResponseDTO response = authRegisterService.registerClientAndAddNumero(
                username, password, valeurNumero, plateformeId
        );

        if (response != null && response.isSuccess()) {
            model.addAttribute("success", response.getMessage());
            return "register"; 
        } else {
            model.addAttribute("error", response != null ? response.getMessage() : "Erreur inconnue");
            return "register";
        }
    }
}
