package sms.client.dto.transaction;

public class TransactionAvecNumeroRequestDTO {
    
    private Long idNumeroExpediteur;
    private Long idNumeroDestinataire;
    private Long idMessage;
    private String reference;
    private Double montant;
    private String numero;

    // Constructeurs
    public TransactionAvecNumeroRequestDTO() {
    }

    public TransactionAvecNumeroRequestDTO(Long idNumeroExpediteur, 
                                           Long idNumeroDestinataire, 
                                           Long idMessage, 
                                           String reference, 
                                           Double montant, 
                                           String numero) {
        this.idNumeroExpediteur = idNumeroExpediteur;
        this.idNumeroDestinataire = idNumeroDestinataire;
        this.idMessage = idMessage;
        this.reference = reference;
        this.montant = montant;
        this.numero = numero;
    }

    // Getters et Setters
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

    public Double getMontant() {
        return montant;
    }

    public void setMontant(Double montant) {
        this.montant = montant;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    @Override
    public String toString() {
        return "TransactionAvecNumeroRequestDTO{" +
                "idNumeroExpediteur=" + idNumeroExpediteur +
                ", idNumeroDestinataire=" + idNumeroDestinataire +
                ", idMessage=" + idMessage +
                ", reference='" + reference + '\'' +
                ", montant=" + montant +
                ", numero='" + numero + '\'' +
                '}';
    }
}