package sms.back_end.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "evenement_transaction")
public class EvenementTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* =====================
       Relations
    ===================== */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_message_envoye", nullable = false)
    private MessageEnvoye messageEnvoye;

    /* =====================
       Champs métier
    ===================== */

    @Column(name = "reference", nullable = false, unique = true, length = 100)
    private String reference;

    @Column(name = "montant", nullable = false, precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_creation", updatable = false)
    private LocalDateTime dateCreation;

    /* =====================
       Constructors
    ===================== */

    public EvenementTransaction() {
    }

    public EvenementTransaction( MessageEnvoye messageEnvoye,
                                String reference, BigDecimal montant) {
        this.messageEnvoye = messageEnvoye;
        this.reference = reference;
        this.montant = montant;
    }

    /* =====================
       Lifecycle
    ===================== */

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

    /* =====================
       Getters & Setters
    ===================== */

    public Long getId() {
        return id;
    }

    public MessageEnvoye getMessageEnvoye() {
        return messageEnvoye;
    }

    public void setMessageEnvoye(MessageEnvoye messageEnvoye) {
        this.messageEnvoye = messageEnvoye;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }
}
