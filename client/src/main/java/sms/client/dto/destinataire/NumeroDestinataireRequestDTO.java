package sms.client.dto.destinataire;

public class NumeroDestinataireRequestDTO {

    private String valeur;
    private PlateformeDTO plateforme;
    private UserDTO user;

    // getters & setters
    public String getValeur() {
        return valeur;
    }

    public void setValeur(String valeur) {
        this.valeur = valeur;
    }

    public PlateformeDTO getPlateforme() {
        return plateforme;
    }

    public void setPlateforme(PlateformeDTO plateforme) {
        this.plateforme = plateforme;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public static class PlateformeDTO {
        private int id;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
    }

    public static class UserDTO {
        private int id;

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
    }
}
