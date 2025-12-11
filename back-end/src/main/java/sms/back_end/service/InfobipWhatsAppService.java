package sms.back_end.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sms.back_end.dto.Infobip.InfobipMessageResponseDTO;
import sms.back_end.dto.Infobip.InfobipStatusDTO;
import sms.back_end.dto.InfobipInfoDTO;
import sms.back_end.dto.SmsRequestDTO;          // réutilisé (même DTO que SMS pour l'instant)
import sms.back_end.dto.SmsSendResponseDTO;     // réutilisé
import sms.back_end.entity.InfobipInfo;
import sms.back_end.entity.MessageEnvoye;
import sms.back_end.entity.NumeroDestinataire;
import sms.back_end.entity.NumeroExpediteur;
import sms.back_end.entity.SmsMessage;
import sms.back_end.repository.MessageEnvoyeRepository;
import sms.back_end.repository.NumeroDestinataireRepository;
import sms.back_end.repository.NumeroExpediteurRepository;

@Service
public class InfobipWhatsAppService {

    private final RestTemplate restTemplate;
    private final NumeroExpediteurRepository numeroExpediteurRepo;
    private final NumeroDestinataireRepository numeroDestinataireRepo;
    private final SmsMessageService smsMessageService;
    private final MessageEnvoyeRepository messageEnvoyeRepo;

    public InfobipWhatsAppService(RestTemplate restTemplate,
                                  NumeroExpediteurRepository numeroExpediteurRepo,
                                  NumeroDestinataireRepository numeroDestinataireRepo,
                                  SmsMessageService smsMessageService,
                                  MessageEnvoyeRepository messageEnvoyeRepo) {
        this.restTemplate = restTemplate;
        this.numeroExpediteurRepo = numeroExpediteurRepo;
        this.numeroDestinataireRepo = numeroDestinataireRepo;
        this.smsMessageService = smsMessageService;
        this.messageEnvoyeRepo = messageEnvoyeRepo;
    }

    public SmsSendResponseDTO sendWhatsApp(SmsRequestDTO dto) {
        SmsSendResponseDTO responseDTO = new SmsSendResponseDTO();

        try {
            // 1️⃣ Chargement des entités (identique à SMS)
            NumeroExpediteur expediteur = numeroExpediteurRepo.findById(dto.getIdNumeroExpediteur())
                    .orElseThrow(() -> new RuntimeException("Numéro expéditeur introuvable"));

            NumeroDestinataire destinataire = numeroDestinataireRepo.findById(dto.getIdNumeroDestinataire())
                    .orElseThrow(() -> new RuntimeException("Numéro destinataire introuvable"));

            SmsMessage smsMessage = smsMessageService.getMessageById(dto.getIdMessage())
                    .orElseThrow(() -> new RuntimeException("Message introuvable"));

            InfobipInfo infobip = expediteur.getInfobipInfo();

            // Endpoint WhatsApp Infobip
            String url = "https://" + infobip.getBaseUrl() + "/whatsapp/1/message/text";

            // 2️⃣ Headers (identique)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "App " + infobip.getApiKey());
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // 3️⃣ Body spécifique WhatsApp (texte simple)
            Map<String, Object> body = new HashMap<>();
            body.put("from", expediteur.getValeur());        // Numéro WhatsApp enregistré chez Infobip
            body.put("to", destinataire.getValeur());
            body.put("content", Map.of(
                "text", smsMessage.getTexte()
            ));

            // 4️⃣ Logs
            System.out.println("===== ENVOI WHATSAPP =====");
            System.out.println("FROM: " + expediteur.getValeur());
            System.out.println("TO  : " + destinataire.getValeur());
            System.out.println("TEXT: " + smsMessage.getTexte());
            System.out.println("==========================");

            // 5️⃣ Envoi HTTP
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    new HttpEntity<>(body, headers),
                    String.class
            );

            String responseBody = response.getBody();
            System.out.println("=== REPONSE INFOBIP WHATSAPP === " + responseBody);

            // 6️⃣ Parsing de la réponse (structure légèrement différente mais messageId et status existent)
            if (responseBody != null && responseBody.trim().startsWith("{")) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(responseBody);

                // Pour WhatsApp, la réponse est directement sur le message (pas dans un tableau "messages")
                JsonNode messageNode = root.path("messages").isArray() 
                    ? root.path("messages").get(0) 
                    : root; // parfois directement à la racine selon le type de message

                InfobipMessageResponseDTO infobipResponse = new InfobipMessageResponseDTO();
                infobipResponse.setMessageId(messageNode.path("messageId").asText());
                infobipResponse.setTo(messageNode.path("to").asText());

                JsonNode statusNode = messageNode.path("status");
                if (!statusNode.isMissingNode()) {
                    InfobipStatusDTO status = new InfobipStatusDTO();
                    status.setGroupId(statusNode.path("groupId").asInt());
                    status.setGroupName(statusNode.path("groupName").asText());
                    status.setId(statusNode.path("id").asInt());
                    status.setName(statusNode.path("name").asText());
                    status.setDescription(statusNode.path("description").asText());
                    infobipResponse.setStatus(status);

                    responseDTO.setStatut(status.getName());
                    responseDTO.setDescription(status.getDescription());
                }

                responseDTO.setInfobipResponse(infobipResponse);

                // 7️⃣ Enregistrement dans message_envoye (exactement comme pour SMS)
                MessageEnvoye messageEnvoye = new MessageEnvoye();
                messageEnvoye.setIdNumeroExpediteur(expediteur.getId());
                messageEnvoye.setIdNumeroDestinataire(destinataire.getIdNumero());
                messageEnvoye.setIdMessage(smsMessage.getId());
                messageEnvoye.setInfobipMessageId(infobipResponse.getMessageId());
                messageEnvoyeRepo.save(messageEnvoye);
            }

            // 8️⃣ Remplissage du DTO de réponse (identique)
            responseDTO.setIdNumeroExpediteur(expediteur.getId());
            responseDTO.setNumeroExpediteur(expediteur.getValeur());
            responseDTO.setIdNumeroDestinataire(destinataire.getIdNumero());
            responseDTO.setNumeroDestinataire(destinataire.getValeur());
            responseDTO.setIdMessage(smsMessage.getId());
            responseDTO.setMessage(smsMessage.getTexte());
            responseDTO.setInfobipInfo(new InfobipInfoDTO(infobip));

        } catch (HttpClientErrorException e) {
            System.err.println("=== ERREUR INFOBIP WHATSAPP ===");
            System.err.println("Status: " + e.getStatusCode());
            System.err.println("Body: " + e.getResponseBodyAsString());
            responseDTO.setStatut("ERROR");
            responseDTO.setDescription(e.getResponseBodyAsString());
        } catch (Exception e) {
            System.err.println("=== ERREUR GÉNÉRALE WHATSAPP ===");
            e.printStackTrace();
            responseDTO.setStatut("ERROR");
            responseDTO.setDescription(e.getMessage());
        }

        return responseDTO;
    }
}