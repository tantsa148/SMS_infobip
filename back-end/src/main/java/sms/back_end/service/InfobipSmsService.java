package sms.back_end.service;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import sms.back_end.dto.InfobipResponseDTO;
import sms.back_end.dto.SmsFromUserDetailDTO;

@Service
public class InfobipSmsService {

    public InfobipResponseDTO sendSmsFromDto(SmsFromUserDetailDTO smsDTO) {
        try {
            WebClient client = WebClient.builder()
                    .baseUrl("https://" + smsDTO.getBaseUrl())
                    .defaultHeader(HttpHeaders.AUTHORIZATION, "App " + smsDTO.getApiKey())
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, "application/json")
                    .build();

            String jsonPayload = "{ \"messages\": [ { " +
                    "\"from\": \"" + smsDTO.getFrom() + "\", " +
                    "\"destinations\": [{ \"to\": \"" + smsDTO.getTo() + "\" }], " +
                    "\"text\": \"" + smsDTO.getMessage() + "\" } ] }";

            String rawResponse = client.post()
                    .uri("/sms/2/text/advanced")
                    .bodyValue(jsonPayload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Parser le JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(rawResponse);
            JsonNode msg = root.path("messages").get(0);

            InfobipResponseDTO response = new InfobipResponseDTO();
            response.setMessageId(msg.path("messageId").asText());
            response.setTo(msg.path("to").asText());
            response.setStatusName(msg.path("status").path("name").asText());
            response.setStatusDescription(msg.path("status").path("description").asText());

            return response;

        } catch (WebClientResponseException ex) {
            throw new RuntimeException("Erreur Infobip : " + ex.getResponseBodyAsString());
        } catch (Exception ex) {
            throw new RuntimeException("Erreur inattendue : " + ex.getMessage());
        }
    }
}
