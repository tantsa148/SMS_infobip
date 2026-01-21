package sms.client.dto.otp;

/**
 * DTO pour la demande d'envoi OTP
 */
public class OtpSendRequestDTO {
    private Long idNumeroExpediteur;
    private Long idNumeroDestinataire;
    private Long idMessage;

    public OtpSendRequestDTO() {
    }

    public OtpSendRequestDTO(Long idNumeroExpediteur, Long idNumeroDestinataire, Long idMessage) {
        this.idNumeroExpediteur = idNumeroExpediteur;
        this.idNumeroDestinataire = idNumeroDestinataire;
        this.idMessage = idMessage;
    }

    public Long getIdNumeroExpediteur() {
        return idNumeroExpediteur;
    }

    public void setIdNumeroExpediteur(Long idNumeroExpediteur) {
        this.idNumeroExpediteur = idNumeroExpediteur;
    }

    public Long getIdNumeroDestinataire() {
        return idNumeroDestinataire;
    }

    public void setIdNumeroDestinataire(Long idNumeroDestinataire) {
        this.idNumeroDestinataire = idNumeroDestinataire;
    }

    public Long getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
    }
}

