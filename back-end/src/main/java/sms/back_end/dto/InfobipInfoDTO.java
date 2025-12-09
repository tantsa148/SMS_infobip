package sms.back_end.dto;

import java.util.List;
import java.util.stream.Collectors;

import sms.back_end.entity.InfobipInfo;
import sms.back_end.entity.NumeroExpediteur;

public class InfobipInfoDTO {

    private Long id;
    private String apiKey;
    private String baseUrl;
    private List<String> numeros; // Liste des numéros en String

    public InfobipInfoDTO() {}

    // Constructeur à partir de l'entité
    public InfobipInfoDTO(InfobipInfo entity) {
        this.id = entity.getId();
        this.apiKey = entity.getApiKey();
        this.baseUrl = entity.getBaseUrl();
        if (entity.getNumerosExpediteurs() != null) {
            this.numeros = entity.getNumerosExpediteurs().stream()
                                 .map(NumeroExpediteur::getValeur)
                                 .collect(Collectors.toList());
        }
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public List<String> getNumeros() { return numeros; }
    public void setNumeros(List<String> numeros) { this.numeros = numeros; }
}
