package sms.back_end.service;

import java.io.ByteArrayInputStream;
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
import sms.back_end.entity.MessageDetail;
import sms.back_end.repository.HistoriqueRepository;

@Service
public class HistoriqueService {

    private final HistoriqueRepository historiqueRepository;
    private final MessageDetailService messageDetailService;

    private final HistoriqueExportService historiqueExportService;

    public HistoriqueService(HistoriqueRepository historiqueRepository,
                            MessageDetailService messageDetailService,
                            HistoriqueExportService historiqueExportService) {
        this.historiqueRepository = historiqueRepository;
        this.messageDetailService = messageDetailService;
        this.historiqueExportService = historiqueExportService;
    }


    /**
     * Récupère l'historique pour un utilisateur donné
     */
    public List<Historique> getHistoriqueByUserId(Long userId) {
        System.out.println("[HistoriqueService] Récupération historique pour userId: " + userId);
        try {
            List<Historique> historiques = historiqueRepository.findByIdUtilisateur(userId);
            System.out.println("[HistoriqueService] Nombre d'historiques trouvés: " + historiques.size());
            return historiques;
        } catch (Exception e) {
            System.err.println("[HistoriqueService] ERREUR lors de la récupération de l'historique:");
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la récupération de l'historique: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les détails d'un SMS sur Infobip via le messageId et la baseUrl
     * ET enregistre les détails en base de données avec la relation MessageEnvoye
     */
    public JsonNode getSmsDetailsFromInfobip(Historique historique) {
        System.out.println("========================================");
        System.out.println("[HistoriqueService] DÉBUT getSmsDetailsFromInfobip");
        System.out.println("[HistoriqueService] idEnvoi: " + historique.getIdEnvoi());
        System.out.println("[HistoriqueService] messageId: " + historique.getInfobipMessageId());
        System.out.println("[HistoriqueService] baseUrl: " + historique.getInfobipBaseUrl());
        
        String url = historique.getInfobipBaseUrl();
        if (url == null || url.isBlank()) {
            System.err.println("[HistoriqueService] ERREUR: baseUrl est null ou vide");
            throw new RuntimeException("baseUrl Infobip manquante");
        }
        
        if (!url.startsWith("http")) {
            url = "https://" + url;
            System.out.println("[HistoriqueService] URL modifiée avec https: " + url);
        }
        
        url += "/sms/2/logs?messageId=" + historique.getInfobipMessageId();
        System.out.println("[HistoriqueService] URL complète: " + url);
        System.out.println("[HistoriqueService] API Key: " + 
            (historique.getInfobipApiKey() != null ? "***présente***" : "NULL"));

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "App " + historique.getInfobipApiKey());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            System.out.println("[HistoriqueService] Envoi de la requête à Infobip...");
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            System.out.println("[HistoriqueService] Status code: " + response.getStatusCode());
            JsonNode body = response.getBody();
            System.out.println("[HistoriqueService] Body reçu: " + body);

            // Sauvegarde des détails en base de données
            if (body != null) {
                System.out.println("[HistoriqueService] Body non null, vérification du contenu...");
                
                JsonNode messageToSave = null;
                
                // Cas 1: results est un tableau (ancienne structure)
                if (body.has("results") && body.get("results").isArray()) {
                    JsonNode results = body.get("results");
                    System.out.println("[HistoriqueService] Structure: results est un tableau");
                    System.out.println("[HistoriqueService] Nombre de résultats: " + results.size());
                    
                    if (results.size() > 0) {
                        messageToSave = results.get(0);
                    }
                } 
                // Cas 2: results est un objet avec "result" (votre structure)
                else if (body.has("results") && body.get("results").isObject()) {
                    JsonNode results = body.get("results");
                    System.out.println("[HistoriqueService] Structure: results est un objet");
                    
                    if (results.has("result")) {
                        messageToSave = results.get("result");
                        System.out.println("[HistoriqueService] Message extrait de results.result");
                    }
                } else {
                    System.err.println("[HistoriqueService] WARNING: Structure results non reconnue");
                    System.err.println("[HistoriqueService] Structure reçue: " + body);
                }
                
                // Sauvegarde si un message a été trouvé
                if (messageToSave != null) {
                    System.out.println("[HistoriqueService] Message à sauvegarder: " + messageToSave);
                    try {
                        System.out.println("[HistoriqueService] Tentative de sauvegarde des détails...");
                        // 🆕 Le MessageDetailService va automatiquement chercher et lier le MessageEnvoye
                        // via l'infobip_message_id présent dans le JSON
                        MessageDetail savedDetail = messageDetailService.saveMessageDetail(messageToSave);
                        System.out.println("[HistoriqueService] ✓ Détails SMS sauvegardés avec succès! ID: " 
                                            + savedDetail.getId());
                        
                        // 🆕 Vérification de la liaison
                        if (savedDetail.getMessageEnvoye() != null) {
                            System.out.println("[HistoriqueService] ✓ Relation MessageEnvoye établie! ID: " 
                                                + savedDetail.getMessageEnvoye().getId());
                        } else {
                            System.out.println("[HistoriqueService] ⚠ Aucune relation MessageEnvoye trouvée " +
                                             "(le message n'existe peut-être pas encore dans message_envoye)");
                        }
                    } catch (Exception saveEx) {
                        System.err.println("[HistoriqueService] ✗ ERREUR lors de la sauvegarde des détails:");
                        System.err.println("[HistoriqueService] Type: " + saveEx.getClass().getName());
                        System.err.println("[HistoriqueService] Message: " + saveEx.getMessage());
                        saveEx.printStackTrace();
                        // Ne pas bloquer le retour de la réponse - on continue
                        System.out.println("[HistoriqueService] Continuation malgré l'erreur de sauvegarde");
                    }
                } else {
                    System.err.println("[HistoriqueService] WARNING: Aucun message trouvé à sauvegarder");
                }
            } else {
                System.err.println("[HistoriqueService] WARNING: Body est null");
            }

            System.out.println("[HistoriqueService] FIN getSmsDetailsFromInfobip - Succès");
            System.out.println("========================================");
            return body;
            
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("[HistoriqueService] ✗✗✗ ERREUR CRITIQUE ✗✗✗");
            System.err.println("[HistoriqueService] idEnvoi: " + historique.getIdEnvoi());
            System.err.println("[HistoriqueService] messageId: " + historique.getInfobipMessageId());
            System.err.println("[HistoriqueService] Type d'erreur: " + e.getClass().getName());
            System.err.println("[HistoriqueService] Message: " + e.getMessage());
            System.err.println("[HistoriqueService] Stack trace:");
            e.printStackTrace();
            System.err.println("========================================");
            throw new RuntimeException("Impossible de récupérer les détails du SMS: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère les détails d'un message WhatsApp sur Infobip
     * ET enregistre les détails en base de données avec la relation MessageEnvoye
     */
    public JsonNode getWhatsappMessageDetails(Historique historique) {
        System.out.println("========================================");
        System.out.println("[HistoriqueService] DÉBUT getWhatsappMessageDetails");
        System.out.println("[HistoriqueService] idEnvoi: " + historique.getIdEnvoi());
        
        String baseUrl = historique.getInfobipBaseUrl();
        if (baseUrl == null || baseUrl.isBlank()) {
            System.err.println("[HistoriqueService] ERREUR: baseUrl est null ou vide");
            throw new RuntimeException("baseUrl Infobip manquante");
        }
        
        if (!baseUrl.startsWith("http")) {
            baseUrl = "https://" + baseUrl;
            System.out.println("[HistoriqueService] URL modifiée avec https: " + baseUrl);
        }

        String messageId = historique.getInfobipMessageId();
        if (messageId == null || messageId.isBlank()) {
            System.err.println("[HistoriqueService] ERREUR: messageId WhatsApp manquant");
            throw new RuntimeException("Impossible de récupérer le détail : messageId WhatsApp manquant !");
        }

        String url = baseUrl + "/whatsapp/1/messages/" + messageId;
        System.out.println("[HistoriqueService] URL complète: " + url);
        System.out.println("[HistoriqueService] API Key: " + 
            (historique.getInfobipApiKey() != null ? "***présente***" : "NULL"));

        try {
            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "App " + historique.getInfobipApiKey());
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            System.out.println("[HistoriqueService] Envoi de la requête WhatsApp à Infobip...");
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    JsonNode.class
            );

            System.out.println("[HistoriqueService] Status code: " + response.getStatusCode());
            JsonNode body = response.getBody();
            System.out.println("[HistoriqueService] Body reçu: " + body);

            // Sauvegarde des détails en base de données
            if (body != null) {
                System.out.println("[HistoriqueService] Body non null, tentative de sauvegarde...");
                try {
                    // 🆕 Le MessageDetailService va automatiquement chercher et lier le MessageEnvoye
                    // via l'infobip_message_id présent dans le JSON
                    MessageDetail savedDetail = messageDetailService.saveMessageDetail(body);
                    System.out.println("[HistoriqueService] ✓ Détails WhatsApp sauvegardés avec succès! ID: " 
                                        + savedDetail.getId());
                    
                    // 🆕 Vérification de la liaison
                    if (savedDetail.getMessageEnvoye() != null) {
                        System.out.println("[HistoriqueService] ✓ Relation MessageEnvoye établie! ID: " 
                                            + savedDetail.getMessageEnvoye().getId());
                    } else {
                        System.out.println("[HistoriqueService] ⚠ Aucune relation MessageEnvoye trouvée " +
                                         "(le message n'existe peut-être pas encore dans message_envoye)");
                    }
                } catch (Exception saveEx) {
                    System.err.println("[HistoriqueService] ✗ ERREUR lors de la sauvegarde des détails WhatsApp:");
                    System.err.println("[HistoriqueService] Type: " + saveEx.getClass().getName());
                    System.err.println("[HistoriqueService] Message: " + saveEx.getMessage());
                    saveEx.printStackTrace();
                    // Ne pas bloquer le retour de la réponse
                    System.out.println("[HistoriqueService] Continuation malgré l'erreur de sauvegarde");
                }
            } else {
                System.err.println("[HistoriqueService] WARNING: Body WhatsApp est null");
            }

            System.out.println("[HistoriqueService] FIN getWhatsappMessageDetails - Succès");
            System.out.println("========================================");
            return body;
            
        } catch (Exception e) {
            System.err.println("========================================");
            System.err.println("[HistoriqueService] ✗✗✗ ERREUR CRITIQUE WHATSAPP ✗✗✗");
            System.err.println("[HistoriqueService] idEnvoi: " + historique.getIdEnvoi());
            System.err.println("[HistoriqueService] messageId: " + historique.getInfobipMessageId());
            System.err.println("[HistoriqueService] Type d'erreur: " + e.getClass().getName());
            System.err.println("[HistoriqueService] Message: " + e.getMessage());
            System.err.println("[HistoriqueService] Stack trace:");
            e.printStackTrace();
            System.err.println("========================================");
            throw new RuntimeException("Impossible de récupérer les détails WhatsApp: " + e.getMessage(), e);
        }
    }
    public ByteArrayInputStream exportHistoriqueCsvByUserId(Long userId) {
    List<Historique> historiques = getHistoriqueByUserId(userId);
    return historiqueExportService.exportToCsv(historiques);
}

}