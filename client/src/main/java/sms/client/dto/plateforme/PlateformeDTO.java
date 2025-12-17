package sms.client.dto.plateforme;

import java.time.LocalDateTime;

public class PlateformeDTO {

    private Long id;
    private String nomPlateforme;
    private LocalDateTime dateCreation;

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomPlateforme() { return nomPlateforme; }
    public void setNomPlateforme(String nomPlateforme) { this.nomPlateforme = nomPlateforme; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
