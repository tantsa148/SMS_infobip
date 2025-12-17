package sms.client.dto.destinataire;

import java.time.LocalDateTime;

public class NumeroDestinataireResponseDTO {

    private Long idNumero;
    private String valeur;
    private LocalDateTime dateCreation;
    private int idPlateforme;
    private int idUser;

    // getters & setters
    public Long getIdNumero() { return idNumero; }
    public void setIdNumero(Long idNumero) { this.idNumero = idNumero; }

    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public int getIdPlateforme() { return idPlateforme; }
    public void setIdPlateforme(int idPlateforme) { this.idPlateforme = idPlateforme; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }
}
