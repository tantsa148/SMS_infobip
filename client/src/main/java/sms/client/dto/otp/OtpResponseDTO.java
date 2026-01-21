package sms.client.dto.otp;

/**
 * DTO pour la réponse de l'API OTP
 */
public class OtpResponseDTO {
    private boolean success;
    private String message;
    private Long idMessageEnvoye;

    public OtpResponseDTO() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getIdMessageEnvoye() {
        return idMessageEnvoye;
    }

    public void setIdMessageEnvoye(Long idMessageEnvoye) {
        this.idMessageEnvoye = idMessageEnvoye;
    }
}

