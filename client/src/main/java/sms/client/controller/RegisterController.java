package sms.client.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import sms.client.dto.plateforme.PlateformeDTO;
import sms.client.dto.user.RegisterFormDTO;
import sms.client.dto.user.RegisterResponseDTO;
import sms.client.service.AuthRegisterService;
import sms.client.service.PlateformeService;

@Controller
public class RegisterController {

    private final AuthRegisterService authRegisterService;
    private final PlateformeService plateformeService;

    public RegisterController(AuthRegisterService authRegisterService,
                              PlateformeService plateformeService) {
        this.authRegisterService = authRegisterService;
        this.plateformeService = plateformeService;
    }

    /* =======================
       AFFICHAGE FORMULAIRE
       ======================= */

@GetMapping("/register")
public String showRegisterForm(Model model) {

    List<PlateformeDTO> plateformes = plateformeService.getPlateformes();
    model.addAttribute("plateformes", plateformes);

    // Créer le DTO avec valeurs par défaut pour SMS
    RegisterFormDTO form = new RegisterFormDTO();
    form.setIdNumeroExpediteur(2L); // expéditeur par défaut
    form.setIdMessage(1L);          // message par défaut
    model.addAttribute("registerForm", form);

    return "register";
}

    /* =======================
       TRAITEMENT FORMULAIRE
       ======================= */
@PostMapping("/register")
public String register(@ModelAttribute("registerForm") RegisterFormDTO form, Model model) {

    // Nom du controller (simple)
    String controllerName = this.getClass().getSimpleName(); // ex: "RegisterController"
    String methodName = "register"; // méthode actuelle

    RegisterResponseDTO response = authRegisterService.registerClientAndAddNumero(
            form.getUsername(),
            form.getPassword(),
            form.getValeurNumero(),
            form.getPlateformeId(),
            form.getIdNumeroExpediteur(),
            form.getIdMessage(),
            controllerName, // ajouté
            methodName      // ajouté
    );

    if (response != null && response.isSuccess()) {
        model.addAttribute("success", response.getMessage());
    } else {
        model.addAttribute("error", response != null ? response.getMessage() : "Erreur inconnue");
    }

    return "register";
}

}
