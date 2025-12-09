package sms.back_end.dto;

public class UsersDetailRequest {

    private Long idNumero; // ID du NumeroExpediteur envoyé depuis le JSON

    public UsersDetailRequest() {}

    public UsersDetailRequest(Long idNumero) {
        this.idNumero = idNumero;
    }

    // ============================
    // Getters & Setters
    // ============================
    public Long getIdNumero() { return idNumero; }
    public void setIdNumero(Long idNumero) { this.idNumero = idNumero; }
}
