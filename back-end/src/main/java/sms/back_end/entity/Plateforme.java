package sms.back_end.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "plateforme")
public class Plateforme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // correspond à SERIAL
    private Long id;

    @Column(name = "nom_plateform", length = 50, nullable = false, unique = true)
    private String nomPlateforme;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    public Plateforme() {
        this.dateCreation = LocalDateTime.now();
    }

    public Plateforme(String nomPlateforme) {
        this.nomPlateforme = nomPlateforme;
        this.dateCreation = LocalDateTime.now();
    }

    // Getters et setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNomPlateforme() { return nomPlateforme; }
    public void setNomPlateforme(String nomPlateforme) { this.nomPlateforme = nomPlateforme; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
