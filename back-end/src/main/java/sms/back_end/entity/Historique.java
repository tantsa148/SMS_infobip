package sms.back_end.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "vue_messages_complets") // Mapping direct avec la vue SQL
public class Historique {

    @Id
    @Column(name = "id_envoi")
    private Long idEnvoi;

    @Column(name = "id_utilisateur")
    private Long idUtilisateur;

    @Column(name = "expediteur")
    private String expediteur;

    @Column(name = "numero_expediteur")
    private String numeroExpediteur;

    @Column(name = "numero_destinataire")
    private String numeroDestinataire;

    @Column(name = "message")
    private String message;

    @Column(name = "infobip_message_id")
    private String infobipMessageId;

    @Column(name = "infobip_base_url")
    private String infobipBaseUrl;

    @Column(name = "infobip_api_key")
    private String infobipApiKey;

    @Column(name = "id_plateforme")
    private Long idPlateforme;

    @Column(name = "plateforme")
    private String plateforme;

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi;

    // Getters et Setters

    public Long getIdEnvoi() { return idEnvoi; }
    public void setIdEnvoi(Long idEnvoi) { this.idEnvoi = idEnvoi; }

    public Long getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getExpediteur() { return expediteur; }
    public void setExpediteur(String expediteur) { this.expediteur = expediteur; }

    public String getNumeroExpediteur() { return numeroExpediteur; }
    public void setNumeroExpediteur(String numeroExpediteur) { this.numeroExpediteur = numeroExpediteur; }

    public String getNumeroDestinataire() { return numeroDestinataire; }
    public void setNumeroDestinataire(String numeroDestinataire) { this.numeroDestinataire = numeroDestinataire; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getInfobipMessageId() { return infobipMessageId; }
    public void setInfobipMessageId(String infobipMessageId) { this.infobipMessageId = infobipMessageId; }

    public String getInfobipBaseUrl() { return infobipBaseUrl; }
    public void setInfobipBaseUrl(String infobipBaseUrl) { this.infobipBaseUrl = infobipBaseUrl; }

    public String getInfobipApiKey() { return infobipApiKey; }
    public void setInfobipApiKey(String infobipApiKey) { this.infobipApiKey = infobipApiKey; }

    public Long getIdPlateforme() { return idPlateforme; }
    public void setIdPlateforme(Long idPlateforme) { this.idPlateforme = idPlateforme; }

    public String getPlateforme() { return plateforme; }
    public void setPlateforme(String plateforme) { this.plateforme = plateforme; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
}
