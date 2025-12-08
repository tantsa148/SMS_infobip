package sms.back_end.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "message_envoye")
public class MessageEnvoye {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_numero_expediteur")
    private Long idNumeroExpediteur;

    @Column(name = "id_message")
    private Long idMessage;

    @Column(name = "id_numero_destinataire")
    private Long idNumeroDestinataire;

    @Column(name = "infobip_message_id")
    private String infobipMessageId;

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    // =====================
    // Getters
    // =====================
    public Long getId() {
        return id;
    }

    public Long getIdNumeroExpediteur() {
        return idNumeroExpediteur;
    }

    public Long getIdMessage() {
        return idMessage;
    }

    public Long getIdNumeroDestinataire() {
        return idNumeroDestinataire;
    }

    public String getInfobipMessageId() {
        return infobipMessageId;
    }

    public LocalDateTime getDateEnvoi() {
        return dateEnvoi;
    }

    // =====================
    // Setters
    // =====================
    public void setId(Long id) {
        this.id = id;
    }

    public void setIdNumeroExpediteur(Long idNumeroExpediteur) {
        this.idNumeroExpediteur = idNumeroExpediteur;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
    }

    public void setIdNumeroDestinataire(Long idNumeroDestinataire) {
        this.idNumeroDestinataire = idNumeroDestinataire;
    }

    public void setInfobipMessageId(String infobipMessageId) {
        this.infobipMessageId = infobipMessageId;
    }

    public void setDateEnvoi(LocalDateTime dateEnvoi) {
        this.dateEnvoi = dateEnvoi;
    }
}
