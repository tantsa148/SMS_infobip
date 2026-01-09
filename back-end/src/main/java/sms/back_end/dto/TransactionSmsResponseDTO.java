package sms.back_end.dto;

public class TransactionSmsResponseDTO {

    private String status;
    private String message;
    private String reference;

    public TransactionSmsResponseDTO() {}

    public TransactionSmsResponseDTO(
            String status,
            String message,
            String reference
    ) {
        this.status = status;
        this.message = message;
        this.reference = reference;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getReference() {
        return reference;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
