package sms.back_end.dto;

public class InfobipInfoRequest {

    private String apiKey;
    private String baseUrl;
    private Long idNumero; // ID du NumeroExpediteur envoyé dans le body

    // Getters et setters
    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public Long getIdNumero() { return idNumero; }
    public void setIdNumero(Long idNumero) { this.idNumero = idNumero; }
}
