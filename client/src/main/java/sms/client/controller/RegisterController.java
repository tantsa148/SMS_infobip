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
import sms.client.service.OtpService;
import sms.client.service.PlateformeService;

@Controller
public class RegisterController {

    private final AuthRegisterService authRegisterService;
    private final PlateformeService plateformeService;
  
    public RegisterController(
            AuthRegisterService authRegisterService,
            PlateformeService plateformeService,
            OtpService otpService) {

        this.authRegisterService = authRegisterService;
        this.plateformeService = plateformeService;

    }

    /* =======================
       AFFICHAGE FORMULAIRE
       ======================= */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {

        List<PlateformeDTO> plateformes =
                plateformeService.getPlateformes();

        model.addAttribute("plateformes", plateformes);

        // DTO simple (plus de message / expéditeur ici)
        model.addAttribute("registerForm", new RegisterFormDTO());

        return "register";
    }

    /* =======================
       TRAITEMENT FORMULAIRE
       ======================= */
    @PostMapping("/register")
    public String register(
            @ModelAttribute("registerForm") RegisterFormDTO form,
            Model model) {

        String controllerName =
                this.getClass().getSimpleName(); // RegisterController

        String methodName = "register"; // DOIT correspondre à methode en DB

        RegisterResponseDTO response =
                authRegisterService.registerClientAndAddNumero(
                        form.getUsername(),
                        form.getPassword(),
                        form.getValeurNumero(),
                        form.getPlateformeId(),
                        controllerName,
                        methodName
                );

        if (response != null && response.isSuccess()) {
            model.addAttribute("success", response.getMessage());
            // Passer l'idMessageEnvoye pour la vérification OTP
            if (response.getIdMessageEnvoye() != null) {
                model.addAttribute("idMessageEnvoye", response.getIdMessageEnvoye());
            }
        } else {
            model.addAttribute(
                    "error",
                    response != null
                            ? response.getMessage()
                            : "Erreur inconnue"
            );
        }

        return "register";
    }

}
