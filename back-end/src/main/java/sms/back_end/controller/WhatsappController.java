package sms.back_end.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import sms.back_end.dto.SmsRequestDTO;
import sms.back_end.dto.SmsSendResponseDTO;
import sms.back_end.service.InfobipWhatsAppService;

@RestController
@RequestMapping("/api/whatsapp")
public class WhatsappController {

    private final InfobipWhatsAppService whatsappService;

    public WhatsappController(InfobipWhatsAppService whatsappService) {
        this.whatsappService = whatsappService;
    }

    @PostMapping("/send")
    public ResponseEntity<SmsSendResponseDTO> sendWhatsapp(@Valid @RequestBody SmsRequestDTO whatsappRequest) {

        // Le service renvoie déjà un DTO avec statut ERROR en cas de problème
        SmsSendResponseDTO response = whatsappService.sendWhatsApp(whatsappRequest);

        // Si succès ou statut "PENDING"/"SENT" → 200 OK
        // Si erreur technique ou Infobip → 400 ou 500 selon le cas
        if ("ERROR".equalsIgnoreCase(response.getStatut())) {
            // Erreur métier ou validation Infobip
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            // Ou HttpStatus.INTERNAL_SERVER_ERROR si tu veux distinguer les erreurs serveur
        }

        // Succès
        return ResponseEntity.ok(response);
    }

    // Optionnel : endpoint de test rapide
    @GetMapping("/test")
    public String test() {
        return "WhatsApp API is up and running!";
    }
}