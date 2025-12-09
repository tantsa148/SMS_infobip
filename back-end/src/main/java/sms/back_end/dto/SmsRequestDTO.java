package sms.back_end.dto;

public class SmsRequestDTO {

    private Long idNumeroExpediteur;
    private Long idNumeroDestinataire;
    private Long idMessage;   // ✅ nouveau
    private String message;   // optionnel si tu veux garder l’ancienne compatibilité

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
