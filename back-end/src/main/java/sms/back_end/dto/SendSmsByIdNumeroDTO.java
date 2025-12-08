package sms.back_end.dto;
public class SendSmsByIdNumeroDTO {

    private Long idNumero;
    private Long idDestinataire;
    private Long idMessage;   // ✅ NOUVEAU

    // getters & setters
    public Long getIdNumero() {
        return idNumero;
    }

    public void setIdNumero(Long idNumero) {
        this.idNumero = idNumero;
    }

    public Long getIdDestinataire() {
        return idDestinataire;
    }

    public void setIdDestinataire(Long idDestinataire) {
        this.idDestinataire = idDestinataire;
    }

    public Long getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
    }
}
