package sms.client.dto.sms;

public class SmsRequest {
    private Long idNumeroExpediteur;
    private Long idNumeroDestinataire;
    private Long idMessage;

    // Constructeur par défaut
    public SmsRequest() {
    }

    // Constructeur avec paramètres
    public SmsRequest(Long idNumeroExpediteur, Long idNumeroDestinataire, Long idMessage) {
        this.idNumeroExpediteur = idNumeroExpediteur;
        this.idNumeroDestinataire = idNumeroDestinataire;
        this.idMessage = idMessage;
    }

    // Getters
    public Long getIdNumeroExpediteur() {
        return idNumeroExpediteur;
    }

    public Long getIdNumeroDestinataire() {
        return idNumeroDestinataire;
    }

    public Long getIdMessage() {
        return idMessage;
    }

    // Setters
    public void setIdNumeroExpediteur(Long idNumeroExpediteur) {
        this.idNumeroExpediteur = idNumeroExpediteur;
    }

    public void setIdNumeroDestinataire(Long idNumeroDestinataire) {
        this.idNumeroDestinataire = idNumeroDestinataire;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
    }

    // Optionnel : Méthodes toString, equals, hashCode
}