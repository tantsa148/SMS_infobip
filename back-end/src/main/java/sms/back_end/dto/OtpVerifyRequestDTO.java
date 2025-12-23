package sms.back_end.dto;

public class OtpVerifyRequestDTO {

    private Long idMessageEnvoye; // lien vers le message_envoye
    private String code;          // code OTP que l'utilisateur saisit

    // Getters et setters
    public Long getIdMessageEnvoye() { return idMessageEnvoye; }
    public void setIdMessageEnvoye(Long idMessageEnvoye) { this.idMessageEnvoye = idMessageEnvoye; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
