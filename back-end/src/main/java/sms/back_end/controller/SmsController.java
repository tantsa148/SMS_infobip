package sms.back_end.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.SmsRequestDTO;
import sms.back_end.dto.SmsSendResponseDTO;
import sms.back_end.service.InfobipSmsService;

@RestController
@RequestMapping("/api/sms")
public class SmsController {

    private final InfobipSmsService smsService;

    public SmsController(InfobipSmsService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/send")
    public ResponseEntity<SmsSendResponseDTO> sendSms(@RequestBody SmsRequestDTO smsRequest) {

        // ✅ Validation pour les IDs
        if (smsRequest.getIdNumeroExpediteur() == null) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("ID numéro expéditeur requis"));
        }

        if (smsRequest.getIdNumeroDestinataire() == null) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("ID numéro destinataire requis"));
        }

        if (smsRequest.getIdMessage() == null) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("ID message requis"));
        }

        // ✅ Appel du service
        SmsSendResponseDTO response = smsService.sendSms(smsRequest);
        return ResponseEntity.ok(response);
    }

    // Méthode utilitaire pour créer une réponse d'erreur
    private SmsSendResponseDTO createErrorResponse(String errorMessage) {
        SmsSendResponseDTO errorResponse = new SmsSendResponseDTO();

        errorResponse.setStatut("ERROR");
        errorResponse.setDescription(errorMessage);

        // Tu peux aussi laisser les IDs à null ou 0 si nécessaire
        errorResponse.setIdNumeroExpediteur(0L);
        errorResponse.setIdNumeroDestinataire(0L);
        errorResponse.setIdMessage(0L);

        return errorResponse;
    }
}
