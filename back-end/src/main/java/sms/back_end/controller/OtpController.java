package sms.back_end.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.OtpVerifyRequestDTO;
import sms.back_end.dto.SmsRequestDTO;
import sms.back_end.service.OtpService;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    @PostMapping("/send")
    public String sendOtp(@RequestBody SmsRequestDTO request) {
        otpService.generateAndSendOtp(
                request.getIdNumeroExpediteur(),
                request.getIdNumeroDestinataire(),
                request.getIdMessage()
        );
        return "OTP générée et envoyée avec succès";
    }
     
     @PostMapping("/verify")
public String verifyOtp(@RequestBody OtpVerifyRequestDTO request) {
    try {
        boolean valid = otpService.verifyOtp(request.getIdMessageEnvoye(), request.getCode());
        if (valid) {
            System.out.println("OTP vérifié avec succès pour idMessageEnvoye = " + request.getIdMessageEnvoye());
            return "OTP vérifié avec succès";
        } else {
            System.out.println("OTP invalide pour idMessageEnvoye = " + request.getIdMessageEnvoye());
            return "OTP invalide"; // jamais atteint si exception levée
        }
    } catch (RuntimeException e) {
        // Affiche l'erreur complète dans la console
        System.out.println("=== ERREUR OTP ===");
        e.printStackTrace();              // Affiche la stacktrace complète
        System.out.println("Message d'erreur : " + e.getMessage()); // Message simplifié
        return "Erreur: " + e.getMessage(); // Retourne aussi à Postman
    } catch (Exception e) {
        System.out.println("=== ERREUR INATTENDUE ===");
        e.printStackTrace();
        return "Erreur inattendue: " + e.getMessage();
    }
}

}
