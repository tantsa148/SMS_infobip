package sms.back_end.dto.Expediteur;

import java.time.LocalDateTime;

import sms.back_end.dto.InfobipInfoDTO;
import sms.back_end.entity.NumeroExpediteur;
import sms.back_end.entity.Plateforme;

public class NumeroExpediteurDTO {

    private Long id;
    private String valeur;
    private LocalDateTime dateCreation;

    private InfobipInfoDTO infobipInfo;  // ✅ seulement les infos essentielles
    private Plateforme plateforme;       // ou créer un PlateformeDTO si besoin

    public NumeroExpediteurDTO() {}

    public NumeroExpediteurDTO(NumeroExpediteur entity) {
        this.id = entity.getId();
        this.valeur = entity.getValeur();
        this.dateCreation = entity.getDateCreation();

        if (entity.getInfobipInfo() != null) {
            this.infobipInfo = new InfobipInfoDTO(entity.getInfobipInfo());
        }

        this.plateforme = entity.getPlateforme(); // ou un DTO si tu veux éviter la récursion
    }

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public InfobipInfoDTO getInfobipInfo() { return infobipInfo; }
    public void setInfobipInfo(InfobipInfoDTO infobipInfo) { this.infobipInfo = infobipInfo; }

    public Plateforme getPlateforme() { return plateforme; }
    public void setPlateforme(Plateforme plateforme) { this.plateforme = plateforme; }
}
