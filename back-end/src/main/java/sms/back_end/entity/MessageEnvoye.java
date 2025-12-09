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

    @Column(name = "id_numero_expediteur", nullable = false)
    private Long idNumeroExpediteur;

    @Column(name = "id_message", nullable = false)
    private Long idMessage;

    @Column(name = "id_numero_destinataire", nullable = false)
    private Long idNumeroDestinataire;

    @Column(name = "date_envoi")
    private LocalDateTime dateEnvoi = LocalDateTime.now();

    @Column(name = "infobip_message_id")
    private String infobipMessageId;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getIdNumeroExpediteur() { return idNumeroExpediteur; }
    public void setIdNumeroExpediteur(Long idNumeroExpediteur) { this.idNumeroExpediteur = idNumeroExpediteur; }

    public Long getIdMessage() { return idMessage; }
    public void setIdMessage(Long idMessage) { this.idMessage = idMessage; }

    public Long getIdNumeroDestinataire() { return idNumeroDestinataire; }
    public void setIdNumeroDestinataire(Long idNumeroDestinataire) { this.idNumeroDestinataire = idNumeroDestinataire; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }

    public String getInfobipMessageId() { return infobipMessageId; }
    public void setInfobipMessageId(String infobipMessageId) { this.infobipMessageId = infobipMessageId; }
}
