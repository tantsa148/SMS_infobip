package sms.back_end.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.OtpResponseDTO;
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
    public OtpResponseDTO sendOtp(@RequestBody SmsRequestDTO request) {
        Long idMessageEnvoye = otpService.generateAndSendOtp(
                request.getIdNumeroExpediteur(),
                request.getIdNumeroDestinataire(),
                request.getIdMessage()
        );
        return new OtpResponseDTO(true, "OTP générée et envoyée avec succès", idMessageEnvoye);
    }
     
    @PostMapping("/verify")
    public OtpResponseDTO verifyOtp(@RequestBody OtpVerifyRequestDTO request) {
        try {
            boolean valid = otpService.verifyOtp(request.getIdMessageEnvoye(), request.getCode());
            if (valid) {
                System.out.println("OTP vérifié avec succès pour idMessageEnvoye = " + request.getIdMessageEnvoye());
                return new OtpResponseDTO(true, "OTP vérifié avec succès", request.getIdMessageEnvoye());
            } else {
                System.out.println("OTP invalide pour idMessageEnvoye = " + request.getIdMessageEnvoye());
                return new OtpResponseDTO(false, "OTP invalide");
            }
        } catch (RuntimeException e) {
            System.out.println("=== ERREUR OTP ===");
            e.printStackTrace();
            System.out.println("Message d'erreur : " + e.getMessage());
            return new OtpResponseDTO(false, "Erreur: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("=== ERREUR INATTENDUE ===");
            e.printStackTrace();
            return new OtpResponseDTO(false, "Erreur inattendue: " + e.getMessage());
        }
    }

}
