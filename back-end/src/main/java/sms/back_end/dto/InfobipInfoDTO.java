package sms.back_end.dto;

import sms.back_end.entity.InfobipInfo;

public class InfobipInfoDTO {

    private Long id;
    private String apiKey;
    private String baseUrl;
    private Long idNumero; // ID du NumeroExpediteur

    public InfobipInfoDTO(InfobipInfo info) {
        this.id = info.getId();
        this.apiKey = info.getApiKey();
        this.baseUrl = info.getBaseUrl();
        this.idNumero = info.getNumeroExpediteur() != null ? info.getNumeroExpediteur().getId() : null;
    }

    // Getters
    public Long getId() { return id; }
    public String getApiKey() { return apiKey; }
    public String getBaseUrl() { return baseUrl; }
    public Long getIdNumero() { return idNumero; }
}
