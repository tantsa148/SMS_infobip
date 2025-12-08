package sms.back_end.dto;

public class InfobipResponseDTO {

    private String messageId;
    private String statusName;
    private String statusDescription;
    private String to;

    public InfobipResponseDTO() {}

    public InfobipResponseDTO(String messageId, String statusName, String statusDescription, String to) {
        this.messageId = messageId;
        this.statusName = statusName;
        this.statusDescription = statusDescription;
        this.to = to;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
