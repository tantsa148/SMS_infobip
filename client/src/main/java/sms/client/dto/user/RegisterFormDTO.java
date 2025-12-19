package sms.client.dto.user;

public class RegisterFormDTO {

    private String username;
    private String password;
    private String valeurNumero;
    private int plateformeId;

    // SMS
    private Long idNumeroExpediteur; // ✅ utilisé pour l’envoi
    private Long idMessage;          // ✅ message dynamique

    /* =======================
       GETTERS & SETTERS
       ======================= */

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getValeurNumero() {
        return valeurNumero;
    }

    public void setValeurNumero(String valeurNumero) {
        this.valeurNumero = valeurNumero;
    }

    public int getPlateformeId() {
        return plateformeId;
    }

    public void setPlateformeId(int plateformeId) {
        this.plateformeId = plateformeId;
    }

    public Long getIdNumeroExpediteur() {
        return idNumeroExpediteur;
    }

    public void setIdNumeroExpediteur(Long idNumeroExpediteur) {
        this.idNumeroExpediteur = idNumeroExpediteur;
    }

    public Long getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
    }
}
