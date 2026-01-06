package sms.back_end.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "message_detail")
public class MessageDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID Infobip
    @Column(name = "message_id", nullable = false)
    private String messageId;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_message_envoye")
    private MessageEnvoye messageEnvoye;

    @Column(columnDefinition = "TEXT")
    private String text;

    private LocalDateTime sentAt;
    private LocalDateTime doneAt;

    private Integer statusId;
    private String statusName;

    @Column(columnDefinition = "TEXT")
    private String statusDescription;

    @Column(precision = 10, scale = 4)
    private BigDecimal pricePerMessage;

    private String currency;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    /* ================== GETTERS & SETTERS ================== */

    public Long getId() {
        return id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public MessageEnvoye getMessageEnvoye() {
        return messageEnvoye;
    }

    public void setMessageEnvoye(MessageEnvoye messageEnvoye) {
        this.messageEnvoye = messageEnvoye;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getDoneAt() {
        return doneAt;
    }

    public void setDoneAt(LocalDateTime doneAt) {
        this.doneAt = doneAt;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
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

    public BigDecimal getPricePerMessage() {
        return pricePerMessage;
    }

    public void setPricePerMessage(BigDecimal pricePerMessage) {
        this.pricePerMessage = pricePerMessage;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
}
