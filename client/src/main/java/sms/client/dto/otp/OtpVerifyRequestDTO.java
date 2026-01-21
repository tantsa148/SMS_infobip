package sms.client.dto.otp;

/**
 * DTO pour la demande de vérification OTP
 */
public class OtpVerifyRequestDTO {
    private Long idMessageEnvoye;
    private String code;

    public OtpVerifyRequestDTO() {
    }

    public OtpVerifyRequestDTO(Long idMessageEnvoye, String code) {
        this.idMessageEnvoye = idMessageEnvoye;
        this.code = code;
    }

    public Long getIdMessageEnvoye() {
        return idMessageEnvoye;
    }

    public void setIdMessageEnvoye(Long idMessageEnvoye) {
        this.idMessageEnvoye = idMessageEnvoye;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

