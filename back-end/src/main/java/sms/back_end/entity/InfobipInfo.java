package sms.back_end.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "infobip_info")
public class InfobipInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_key", length = 255)
    private String apiKey;

    @Column(name = "base_url", length = 255)
    private String baseUrl;

    @ManyToOne
    @JoinColumn(name = "id_numero", referencedColumnName = "id_numero",
                foreignKey = @ForeignKey(name = "fk_numero"))
    private NumeroExpediteur numeroExpediteur;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public NumeroExpediteur getNumeroExpediteur() { return numeroExpediteur; }
    public void setNumeroExpediteur(NumeroExpediteur numeroExpediteur) { this.numeroExpediteur = numeroExpediteur; }
}
