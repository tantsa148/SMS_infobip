package sms.back_end.dto.Infobip;

public class InfobipMessageResponseDTO {
    private String messageId;
    private String to;
    private InfobipStatusDTO status;

    // getters/setters
    public String getMessageId() { return messageId; }
    public void setMessageId(String messageId) { this.messageId = messageId; }

    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }

    public InfobipStatusDTO getStatus() { return status; }
    public void setStatus(InfobipStatusDTO status) { this.status = status; }
}
