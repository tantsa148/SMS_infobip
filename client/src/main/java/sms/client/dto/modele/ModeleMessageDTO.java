package sms.client.dto.modele;

public class ModeleMessageDTO {

    private Long id;
    private String methode;
    private Long idExpediteur;
    private String valeurExpediteur;
    private Long idMessage;
    private String texteMessage;

    // Getters
    public Long getId() {
        return id;
    }

    public String getMethode() {
        return methode;
    }

    public Long getIdExpediteur() {
        return idExpediteur;
    }

    public String getValeurExpediteur() {
        return valeurExpediteur;
    }

    public Long getIdMessage() {
        return idMessage;
    }

    public String getTexteMessage() {
        return texteMessage;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setMethode(String methode) {
        this.methode = methode;
    }

    public void setIdExpediteur(Long idExpediteur) {
        this.idExpediteur = idExpediteur;
    }

    public void setValeurExpediteur(String valeurExpediteur) {
        this.valeurExpediteur = valeurExpediteur;
    }

    public void setIdMessage(Long idMessage) {
        this.idMessage = idMessage;
    }

    public void setTexteMessage(String texteMessage) {
        this.texteMessage = texteMessage;
    }
}