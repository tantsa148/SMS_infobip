package sms.back_end.dto;

public class ModeleMessageDTO {

    private Long id;
    private String methode;

    private Long idExpediteur;
    private String valeurExpediteur;

    private Long idMessage;
    private String texteMessage;

    // =======================
    // Getters & Setters
    // =======================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMethode() { return methode; }
    public void setMethode(String methode) { this.methode = methode; }

    public Long getIdExpediteur() { return idExpediteur; }
    public void setIdExpediteur(Long idExpediteur) { this.idExpediteur = idExpediteur; }

    public String getValeurExpediteur() { return valeurExpediteur; }
    public void setValeurExpediteur(String valeurExpediteur) { this.valeurExpediteur = valeurExpediteur; }

    public Long getIdMessage() { return idMessage; }
    public void setIdMessage(Long idMessage) { this.idMessage = idMessage; }

    public String getTexteMessage() { return texteMessage; }
    public void setTexteMessage(String texteMessage) { this.texteMessage = texteMessage; }
}
