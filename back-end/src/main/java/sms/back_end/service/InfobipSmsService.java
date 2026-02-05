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
import sms.back_end.dto.SmsRequestDTO;
import sms.back_end.dto.SmsSendResponseDTO;
import sms.back_end.entity.InfobipInfo;
import sms.back_end.entity.MessageEnvoye;
import sms.back_end.entity.NumeroDestinataire;
import sms.back_end.entity.NumeroExpediteur;
import sms.back_end.entity.SmsMessage;
import sms.back_end.repository.MessageEnvoyeRepository;
import sms.back_end.repository.NumeroDestinataireRepository;
import sms.back_end.repository.NumeroExpediteurRepository;

@Service
public class InfobipSmsService {

    private final RestTemplate restTemplate;
    private final NumeroExpediteurRepository numeroExpediteurRepo;
    private final NumeroDestinataireRepository numeroDestinataireRepo;
    private final SmsMessageService smsMessageService;
    private final MessageEnvoyeRepository messageEnvoyeRepo;

    public InfobipSmsService(RestTemplate restTemplate,
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

    public SmsSendResponseDTO sendSms(SmsRequestDTO dto) {
        SmsSendResponseDTO responseDTO = new SmsSendResponseDTO();

        try {
            // 1️⃣ Charger les entités
            NumeroExpediteur expediteur = numeroExpediteurRepo.findById(dto.getIdNumeroExpediteur())
                    .orElseThrow(() -> new RuntimeException("Numéro expéditeur introuvable"));

            NumeroDestinataire destinataire = numeroDestinataireRepo.findById(dto.getIdNumeroDestinataire())
                    .orElseThrow(() -> new RuntimeException("Numéro destinataire introuvable"));

            // Récupérer ou créer le message
            SmsMessage smsMessage;
            if (dto.getIdMessage() != null) {
                // Comportement existant : utiliser l'ID fourni
                smsMessage = smsMessageService.getMessageById(dto.getIdMessage())
                        .orElseThrow(() -> new RuntimeException("Message introuvable"));
            } else if (dto.getMessage() != null && !dto.getMessage().isEmpty()) {
                // NOUVEAU : créer un nouveau message et récupérer son ID
                SmsMessage newMessage = new SmsMessage(dto.getMessage());
                Long newId = smsMessageService.createMessageAndGetId(newMessage);
                smsMessage = smsMessageService.getMessageById(newId).get();
                System.out.println("=== NOUVEAU MESSAGE CRÉÉ === ID: " + newId);
            } else {
                throw new RuntimeException("Message introuvable");
            }

            InfobipInfo infobip = expediteur.getInfobipInfo();
            String url = "https://" + infobip.getBaseUrl() + "/sms/2/text/advanced";

            // 2️⃣ Préparer les headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "App " + infobip.getApiKey());
            headers.setAccept(List.of(MediaType.APPLICATION_JSON));

            // 3️⃣ Préparer le body JSON
            Map<String, Object> destination = new HashMap<>();
            destination.put("to", destinataire.getValeur());

            Map<String, Object> messageObj = new HashMap<>();
            messageObj.put("from", expediteur.getValeur());
            messageObj.put("destinations", List.of(destination));

            // ✅ Utiliser le texte fourni dans le DTO si présent (OTP), sinon celui du message en base
            String texteAAEnvoyer = (dto.getMessage() != null && !dto.getMessage().isEmpty())
                                    ? dto.getMessage()
                                    : smsMessage.getTexte();
            messageObj.put("text", texteAAEnvoyer);

            Map<String, Object> body = new HashMap<>();
            body.put("messages", List.of(messageObj));

            // 4️⃣ Logs
            System.out.println("===== ENVOI SMS =====");
            System.out.println("FROM: " + expediteur.getValeur());
            System.out.println("TO  : " + destinataire.getValeur());
            System.out.println("TEXT: " + texteAAEnvoyer);
            System.out.println("=====================");

            // 5️⃣ Envoi HTTP
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url,
                    new HttpEntity<>(body, headers),
                    String.class
            );

            String responseBody = response.getBody();
            System.out.println("=== BODY INFOBIP === " + responseBody);

            // 6️⃣ Parser JSON
            if (responseBody != null && responseBody.trim().startsWith("{")) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode root = objectMapper.readTree(responseBody);
                JsonNode messagesNode = root.path("messages");

                if (messagesNode.isArray() && messagesNode.size() > 0) {
                    JsonNode firstMessage = messagesNode.get(0);

                    InfobipMessageResponseDTO infobipResponse = new InfobipMessageResponseDTO();
                    infobipResponse.setMessageId(firstMessage.path("messageId").asText());
                    infobipResponse.setTo(firstMessage.path("to").asText());

                    JsonNode statusNode = firstMessage.path("status");
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

                    // 7️⃣ Enregistrer dans message_envoye
                    MessageEnvoye messageEnvoye = new MessageEnvoye();
                    messageEnvoye.setIdNumeroExpediteur(expediteur.getId());
                    messageEnvoye.setIdNumeroDestinataire(destinataire.getIdNumero());
                    messageEnvoye.setIdMessage(smsMessage.getId());
                    messageEnvoye.setInfobipMessageId(infobipResponse.getMessageId());
                    messageEnvoyeRepo.save(messageEnvoye);
                }
            }

            // 8️⃣ Remplir DTO général
            responseDTO.setIdNumeroExpediteur(expediteur.getId());
            responseDTO.setNumeroExpediteur(expediteur.getValeur());
            responseDTO.setIdNumeroDestinataire(destinataire.getIdNumero());
            responseDTO.setNumeroDestinataire(destinataire.getValeur());
            responseDTO.setIdMessage(smsMessage.getId());
            responseDTO.setMessage(texteAAEnvoyer);
            responseDTO.setInfobipInfo(new InfobipInfoDTO(infobip));

        } catch (HttpClientErrorException e) {
            System.err.println("=== ERREUR INFOBIP ===");
            System.err.println("Status: " + e.getStatusCode());
            System.err.println("Body: " + e.getResponseBodyAsString());

            responseDTO.setStatut("ERROR");
            responseDTO.setDescription(e.getResponseBodyAsString());

        } catch (Exception e) {
            System.err.println("=== ERREUR GÉNÉRALE ===");
            e.printStackTrace();

            responseDTO.setStatut("ERROR");
            responseDTO.setDescription(e.getMessage());
        }

        return responseDTO;
    }
}
