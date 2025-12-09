package sms.back_end.dto;

import java.time.LocalDateTime;

import sms.back_end.entity.UsersDetail;

public class UsersDetailDTO {

    private Long idUtilisateur;
    private Long idNumero;
    private String numeroExpediteur; // valeur du numéro
    private LocalDateTime dateCreation;

    // Nouveaux champs pour InfobipInfo
    private Long idInfobip;
    private String apiKey;
    private String baseUrl;

    private Long idUser;
    private String username;
    private String role;
    private LocalDateTime userCreatedAt;


public UsersDetailDTO(UsersDetail entity) {
    this.idUtilisateur = entity.getIdUtilisateur();
    this.idNumero = entity.getIdNumero();
    this.dateCreation = entity.getDateCreation();

    // Numéro expéditeur et InfobipInfo
    if (entity.getNumeroExpediteur() != null) {
        this.numeroExpediteur = entity.getNumeroExpediteur().getValeur();

        if (entity.getNumeroExpediteur().getInfobipInfo() != null) {
            this.idInfobip = entity.getNumeroExpediteur().getInfobipInfo().getId();
            this.apiKey = entity.getNumeroExpediteur().getInfobipInfo().getApiKey();
            this.baseUrl = entity.getNumeroExpediteur().getInfobipInfo().getBaseUrl();
        }
    }

    // Infos utilisateur
    if (entity.getUser() != null) {  // Assure-toi que UsersDetail a un champ User
        this.idUser = entity.getUser().getId();
        this.username = entity.getUser().getUsername();
        this.role = entity.getUser().getRole();
        this.userCreatedAt = entity.getUser().getCreatedAt();
    }
}
    // Méthode statique pour transformer une entité en DTO
    public static UsersDetailDTO fromEntity(UsersDetail entity) {
        return new UsersDetailDTO(entity);
    }

    // ============================
    // Getters & Setters
    // ============================
    public Long getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public Long getIdNumero() { return idNumero; }
    public void setIdNumero(Long idNumero) { this.idNumero = idNumero; }

    public String getNumeroExpediteur() { return numeroExpediteur; }
    public void setNumeroExpediteur(String numeroExpediteur) { this.numeroExpediteur = numeroExpediteur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Long getIdInfobip() { return idInfobip; }
    public void setIdInfobip(Long idInfobip) { this.idInfobip = idInfobip; }

    public String getApiKey() { return apiKey; }
    public void setApiKey(String apiKey) { this.apiKey = apiKey; }

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public Long getIdUser() { return idUser; }
    public void setIdUser(Long idUser) { this.idUser = idUser; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public LocalDateTime getUserCreatedAt() { return userCreatedAt; }
    public void setUserCreatedAt(LocalDateTime userCreatedAt) { this.userCreatedAt = userCreatedAt; }

}
