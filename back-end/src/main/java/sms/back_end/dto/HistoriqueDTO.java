package sms.back_end.dto;
import java.time.LocalDateTime;

public class HistoriqueDTO {
 
    private Long idEnvoi;
    private Long idUtilisateur;
    private String expediteur;
    private String numeroExpediteur;
    private String numeroDestinataire;
    private String message;
    private String infobipMessageId;
    private LocalDateTime dateEnvoi;

    // Getters et Setters
    public Long getIdEnvoi() { return idEnvoi; }
    public void setIdEnvoi(Long idEnvoi) { this.idEnvoi = idEnvoi; }

    public Long getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public String getExpediteur() { return expediteur; }
    public void setExpediteur(String expediteur) { this.expediteur = expediteur; }

    public String getNumeroExpediteur() { return numeroExpediteur; }
    public void setNumeroExpediteur(String numeroExpediteur) { this.numeroExpediteur = numeroExpediteur; }

    public String getNumeroDestinataire() { return numeroDestinataire; }
    public void setNumeroDestinataire(String numeroDestinataire) { this.numeroDestinataire = numeroDestinataire; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getInfobipMessageId() { return infobipMessageId; }
    public void setInfobipMessageId(String infobipMessageId) { this.infobipMessageId = infobipMessageId; }

    public LocalDateTime getDateEnvoi() { return dateEnvoi; }
    public void setDateEnvoi(LocalDateTime dateEnvoi) { this.dateEnvoi = dateEnvoi; }
}
