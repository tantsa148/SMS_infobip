package sms.client.dto.destinataire;

import java.time.LocalDateTime;

public class NumeroDestinataireResponseDTO {

    private Long idNumero;
    private String valeur;
    private LocalDateTime dateCreation;
    private PlateformeDTO plateforme;
    private Integer idUser;  // idUser directement dans l'objet principal

    // Getters & Setters
    public Long getIdNumero() { return idNumero; }
    public void setIdNumero(Long idNumero) { this.idNumero = idNumero; }

    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public PlateformeDTO getPlateforme() { return plateforme; }
    public void setPlateforme(PlateformeDTO plateforme) { this.plateforme = plateforme; }

    public Integer getIdUser() { return idUser; }
    public void setIdUser(Integer idUser) { this.idUser = idUser; }

    // Classe imbriquée pour Plateforme
    public static class PlateformeDTO {
        private int id;
        private String nomPlateforme;
        private LocalDateTime dateCreation;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }

        public String getNomPlateforme() { return nomPlateforme; }
        public void setNomPlateforme(String nomPlateforme) { this.nomPlateforme = nomPlateforme; }

        public LocalDateTime getDateCreation() { return dateCreation; }
        public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
    }
}

