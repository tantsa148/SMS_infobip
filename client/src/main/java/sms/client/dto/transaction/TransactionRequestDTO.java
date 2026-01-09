package sms.client.dto.transaction;

import java.math.BigDecimal;

public class TransactionRequestDTO {

    private Long idNumeroExpediteur;
    private Long idNumeroDestinataire;
    private Long idMessage;
    private String reference;
    private BigDecimal montant;

    public TransactionRequestDTO() {}

    public TransactionRequestDTO(Long idNumeroExpediteur, Long idNumeroDestinataire,
                                 Long idMessage, String reference, BigDecimal montant) {
        this.idNumeroExpediteur = idNumeroExpediteur;
        this.idNumeroDestinataire = idNumeroDestinataire;
        this.idMessage = idMessage;
        this.reference = reference;
        this.montant = montant;
    }

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
