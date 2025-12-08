package sms.back_end.dto;

public class SmsFromUserDetailDTO {

    private String apiKey;    // Clé API Infobip
    private String baseUrl;   // URL de base Infobip
    private String from;      // Numéro expéditeur
    private String to;        // Numéro destinataire
    private String message;   // Contenu du SMS

    public SmsFromUserDetailDTO() {}

    public SmsFromUserDetailDTO(String apiKey, String baseUrl, String from, String to, String message) {
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
        this.from = from;
        this.to = to;
        this.message = message;
    }

    // Getters et Setters
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
