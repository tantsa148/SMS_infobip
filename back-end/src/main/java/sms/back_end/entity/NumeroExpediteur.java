package sms.back_end.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "numero_expediteur")
public class NumeroExpediteur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_numero")
    private Long id;

    @Column(name = "valeur_numero", nullable = false, unique = true, length = 20)
    private String valeur;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    // Relation ManyToOne vers InfobipInfo
    @ManyToOne
    @JoinColumn(name = "id_infobip")
    private InfobipInfo infobipInfo;

    // ✅ NOUVELLE RELATION ManyToOne vers Plateforme
    @ManyToOne
    @JoinColumn(name = "id_plateforme") // correspond exactement à la colonne SQL
    private Plateforme plateforme;

    public NumeroExpediteur() {}

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

    // Getters / Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public InfobipInfo getInfobipInfo() {
        return infobipInfo;
    }

    public void setInfobipInfo(InfobipInfo infobipInfo) {
        this.infobipInfo = infobipInfo;
    }

    public Plateforme getPlateforme() {
        return plateforme;
    }

    public void setPlateforme(Plateforme plateforme) {
        this.plateforme = plateforme;
    }
}
