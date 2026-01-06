package sms.back_end.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;

import sms.back_end.entity.MessageDetail;
import sms.back_end.service.MessageDetailService;

@RestController
@RequestMapping("/api/messagedetail")
public class MessageDetailController {

    private final MessageDetailService messageDetailService;

    public MessageDetailController(MessageDetailService messageDetailService) {
        this.messageDetailService = messageDetailService;
    }

    @GetMapping
    public ResponseEntity<?> getAllMessageDetails() {
        try {
            List<MessageDetail> details = messageDetailService.getAll();
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorJson(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMessageDetailById(@PathVariable Long id) {
        try {
            MessageDetail detail = messageDetailService.getById(id);
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorJson(e.getMessage()));
        }
    }

    @GetMapping("/message-envoye/{idMessageEnvoye}")
    public ResponseEntity<?> getMessageDetailsByMessageEnvoyeId(@PathVariable Integer idMessageEnvoye) {
        try {
            System.out.println("[MessageDetailController] Recherche des MessageDetail pour idMessageEnvoye: " + idMessageEnvoye);
            List<MessageDetail> details = messageDetailService.getByMessageEnvoyeId(idMessageEnvoye);
            
            if (details.isEmpty()) {
                System.out.println("[MessageDetailController] Aucun MessageDetail trouvé pour idMessageEnvoye: " + idMessageEnvoye);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorJson("Aucun MessageDetail trouvé pour l'idMessageEnvoye " + idMessageEnvoye));
            }
            
            System.out.println("[MessageDetailController] ✓ " + details.size() + " MessageDetail trouvés");
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            System.err.println("[MessageDetailController] Erreur lors de la recherche: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorJson(e.getMessage()));
        }
    }


    
    private JsonNode createErrorJson(String message) {
        return new com.fasterxml.jackson.databind.ObjectMapper()
                .createObjectNode()
                .put("error", message);
    }
}