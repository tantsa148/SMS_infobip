package sms.back_end.dto;

import java.time.LocalDateTime;

public class NumeroDestinataireDTO {

    private Long idNumero;
    private String valeur;
    private LocalDateTime dateCreation;
    private Long idPlateforme;
    private Long idUser;

    public NumeroDestinataireDTO() {}

    public NumeroDestinataireDTO(Long idNumero, String valeur, LocalDateTime dateCreation, Long idPlateforme, Long idUser) {
        this.idNumero = idNumero;
        this.valeur = valeur;
        this.dateCreation = dateCreation;
        this.idPlateforme = idPlateforme;
        this.idUser = idUser;
    }

    // Getters & Setters
    public Long getIdNumero() { return idNumero; }
    public void setIdNumero(Long idNumero) { this.idNumero = idNumero; }

    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Long getIdPlateforme() { return idPlateforme; }
    public void setIdPlateforme(Long idPlateforme) { this.idPlateforme = idPlateforme; }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }
}
