package sms.back_end.dto.Expediteur;

import sms.back_end.entity.InfobipInfo;

public class NumeroExpediteurRequest {
    private String valeur;  // Obligatoire
    private InfobipInfo infobipInfo;  // Nouveau : objet complet au lieu de Long idInfobip

    // Getters et setters
    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public InfobipInfo getInfobipInfo() {
        return infobipInfo;
    }

    public void setInfobipInfo(InfobipInfo infobipInfo) {
        this.infobipInfo = infobipInfo;
    }
}