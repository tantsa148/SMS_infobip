package sms.back_end.service;

import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import sms.back_end.entity.Historique;
import sms.back_end.repository.HistoriqueRepository;

@Service
public class HistoriqueService {

    private final HistoriqueRepository historiqueRepository;

    // Constructeur pour injection
    public HistoriqueService(HistoriqueRepository historiqueRepository) {
        this.historiqueRepository = historiqueRepository;
    }

    /**
     * Récupère l'historique pour un utilisateur donné
     */
    public List<Historique> getHistoriqueByUserId(Long userId) {
        return historiqueRepository.findByIdUtilisateur(userId);
    }

    /**
     * Récupère les détails d'un SMS sur Infobip via le messageId et la baseUrl
     */
        public JsonNode getSmsDetailsFromInfobip(Historique historique) {
        String url = historique.getInfobipBaseUrl();
        if (!url.startsWith("http")) {
            url = "https://" + url; // ajouter https si absent
        }
        url += "/sms/2/logs?messageId=" + historique.getInfobipMessageId();

        System.out.println("[HistoriqueService] Récupération détails SMS pour idEnvoi: " + historique.getIdEnvoi());
        System.out.println("[HistoriqueService] URL Infobip: " + url);
        System.out.println("[HistoriqueService] API Key utilisée: " + historique.getInfobipApiKey());

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "App " + historique.getInfobipApiKey());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            System.out.println("[HistoriqueService] Status code Infobip: " + response.getStatusCode());
            System.out.println("[HistoriqueService] Body Infobip: " + response.getBody());

            return response.getBody();
        } catch (Exception e) {
            System.err.println("[HistoriqueService] Erreur lors de l'appel à Infobip pour idEnvoi " 
                                + historique.getIdEnvoi());
            e.printStackTrace();
            throw new RuntimeException("Impossible de récupérer les détails du SMS: " + e.getMessage());
        }
    }


public JsonNode getWhatsappMessageDetails(Historique historique) {
    String baseUrl = historique.getInfobipBaseUrl();
    if (!baseUrl.startsWith("http")) {
        baseUrl = "https://" + baseUrl;
    }

    String messageId = historique.getInfobipMessageId();
    if (messageId == null || messageId.isBlank()) {
        throw new RuntimeException("Impossible de récupérer le détail : messageId WhatsApp manquant !");
    }

    // Endpoint correct pour récupérer le contenu du message
    String url = baseUrl + "/whatsapp/1/messages/" + messageId;

    System.out.println("[HistoriqueService] Récupération détails WhatsApp pour idEnvoi: " 
                        + historique.getIdEnvoi());
    System.out.println("[HistoriqueService] URL Infobip: " + url);
    System.out.println("[HistoriqueService] API Key utilisée: " + historique.getInfobipApiKey());

    try {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "App " + historique.getInfobipApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                JsonNode.class
        );

        System.out.println("[HistoriqueService] Status code Infobip: " + response.getStatusCode());
        System.out.println("[HistoriqueService] Body Infobip: " + response.getBody());

        return response.getBody();
    } catch (Exception e) {
        System.err.println("[HistoriqueService] Erreur lors de l'appel WhatsApp à Infobip pour idEnvoi " 
                            + historique.getIdEnvoi());
        e.printStackTrace();
        throw new RuntimeException("Impossible de récupérer les détails WhatsApp: " + e.getMessage());
    }
}

}
