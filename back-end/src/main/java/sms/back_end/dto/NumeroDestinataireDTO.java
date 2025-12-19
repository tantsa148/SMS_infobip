package sms.back_end.dto;

import java.time.LocalDateTime;

import sms.back_end.entity.NumeroDestinataire;
import sms.back_end.entity.Plateforme;

public class NumeroDestinataireDTO {

    private Long idNumero;
    private String valeur;
    private LocalDateTime dateCreation;

    private Plateforme plateforme;  // ✅ Affichage complet de la plateforme
    private Long idUser;

    public NumeroDestinataireDTO() {}

    public NumeroDestinataireDTO(NumeroDestinataire entity) {
    this.idNumero = entity.getIdNumero();
    this.valeur = entity.getValeur();
    this.dateCreation = entity.getDateCreation();
    this.plateforme = entity.getPlateforme();

    if (entity.getUser() != null) {
        this.idUser = entity.getUser().getId(); // ✅ CORRECT
    }
}


    // Getters & Setters
    public Long getIdNumero() { return idNumero; }
    public void setIdNumero(Long idNumero) { this.idNumero = idNumero; }

    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Plateforme getPlateforme() { return plateforme; }
    public void setPlateforme(Plateforme plateforme) { this.plateforme = plateforme; }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }
}
