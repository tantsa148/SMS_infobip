package sms.back_end.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "infobip_info")
public class InfobipInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "api_key", length = 255, nullable = false)
    private String apiKey;

    @Column(name = "base_url", length = 255, nullable = false)
    private String baseUrl;

    // Relation OneToMany vers NumeroExpediteur
    @OneToMany(mappedBy = "infobipInfo", cascade = CascadeType.ALL)
    private List<NumeroExpediteur> numerosExpediteurs;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public List<NumeroExpediteur> getNumerosExpediteurs() { return numerosExpediteurs; }
    public void setNumerosExpediteurs(List<NumeroExpediteur> numerosExpediteurs) {
        this.numerosExpediteurs = numerosExpediteurs;
    }
}
