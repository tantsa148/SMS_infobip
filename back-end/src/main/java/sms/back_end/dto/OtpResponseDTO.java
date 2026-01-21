package sms.back_end.dto;

import java.time.LocalDateTime;

public class OtpResponseDTO {

    private boolean success;
    private String message;
    private Long idMessageEnvoye;
    private LocalDateTime timestamp;

    // Constructeurs
    public OtpResponseDTO() {
        this.timestamp = LocalDateTime.now();
    }

    public OtpResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public OtpResponseDTO(boolean success, String message, Long idMessageEnvoye) {
        this.success = success;
        this.message = message;
        this.idMessageEnvoye = idMessageEnvoye;
        this.timestamp = LocalDateTime.now();
    }

    // Getters et Setters
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

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

