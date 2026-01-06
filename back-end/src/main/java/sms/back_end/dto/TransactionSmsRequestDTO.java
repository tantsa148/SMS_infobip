package sms.back_end.dto;

import java.math.BigDecimal;

public class TransactionSmsRequestDTO {
    
    private Long idNumeroExpediteur;
    private Long idNumeroDestinataire;
    private Long idMessage;
    private String reference;
    private BigDecimal montant;

    // Constructeurs
    public TransactionSmsRequestDTO() {
    }

    // Getters & Setters
    public Long getIdNumeroExpediteur() {
        return idNumeroExpediteur;
    }

    public void setIdNumeroExpediteur(Long idNumeroExpediteur) {
        this.idNumeroExpediteur = idNumeroExpediteur;
    }

    public Long getIdNumeroDestinataire() {
        return idNumeroDestinataire;
    }

    public void setIdNumeroDestinataire(Long idNumeroDestinataire) {
        this.idNumeroDestinataire = idNumeroDestinataire;
    }

    public Long getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
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
}