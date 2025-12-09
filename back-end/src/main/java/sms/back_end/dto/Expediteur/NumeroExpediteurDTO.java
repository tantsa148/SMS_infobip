package sms.back_end.dto.Expediteur;

import java.time.LocalDateTime;

import sms.back_end.entity.NumeroExpediteur;

public class NumeroExpediteurDTO {

    private Long id;
    private String valeur;
    private LocalDateTime dateCreation;
    private Long idInfobip; // Id de l'API associée

    public NumeroExpediteurDTO() {}

    public NumeroExpediteurDTO(NumeroExpediteur entity) {
        this.id = entity.getId();
        this.valeur = entity.getValeur();
        this.dateCreation = entity.getDateCreation();
        this.idInfobip = entity.getInfobipInfo() != null ? entity.getInfobipInfo().getId() : null;
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Long getIdInfobip() { return idInfobip; }
    public void setIdInfobip(Long idInfobip) { this.idInfobip = idInfobip; }
}
