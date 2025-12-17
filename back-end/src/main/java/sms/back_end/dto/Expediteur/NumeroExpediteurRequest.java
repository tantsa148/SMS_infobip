package sms.back_end.dto.Expediteur;

import sms.back_end.entity.InfobipInfo;

public class NumeroExpediteurRequest {

    private String valeur;            // obligatoire
    private String label;             // optionnel
    private InfobipInfo infobipInfo;  // objet Infobip complet
    private Long idPlateforme;        // ID de la plateforme

    // ===== Getters et Setters =====
    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public InfobipInfo getInfobipInfo() {
        return infobipInfo;
    }

    public void setInfobipInfo(InfobipInfo infobipInfo) {
        this.infobipInfo = infobipInfo;
    }

    public Long getIdPlateforme() {
        return idPlateforme;
    }

    public void setIdPlateforme(Long idPlateforme) {
        this.idPlateforme = idPlateforme;
    }
}
