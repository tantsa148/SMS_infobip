package sms.back_end.dto.Expediteur;

public class NumeroExpediteurRequest {

    private String valeur; // Le numéro
    private Long idInfobip; // Optionnel : ID de l'InfobipInfo

    // Getters et setters
    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }

    public Long getIdInfobip() { return idInfobip; }
    public void setIdInfobip(Long idInfobip) { this.idInfobip = idInfobip; }
}
