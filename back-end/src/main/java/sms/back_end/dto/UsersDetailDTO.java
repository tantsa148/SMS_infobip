package sms.back_end.dto;

import java.time.LocalDateTime;

import sms.back_end.entity.UsersDetail;

public class UsersDetailDTO {

    private Long idUtilisateur;
    private String username;
    private String role;
    private LocalDateTime dateCreationUtilisateur;

    private Long idInfobip;
    private String apiKey;
    private String baseUrl;

    private Long idNumero;
    private String valeurNumero;
    private LocalDateTime dateCreationNumero;

    private LocalDateTime dateCreationUsersDetail;

    public UsersDetailDTO() {}

    public static UsersDetailDTO fromEntity(UsersDetail ud) {
        UsersDetailDTO dto = new UsersDetailDTO();

        // User
        dto.idUtilisateur = ud.getUser().getId();
        dto.username = ud.getUser().getUsername();
        dto.role = ud.getUser().getRole();
        dto.dateCreationUtilisateur = ud.getUser().getCreatedAt();

        // Infobip
        dto.idInfobip = ud.getInfobipInfo().getId();
        dto.apiKey = ud.getInfobipInfo().getApiKey();
        dto.baseUrl = ud.getInfobipInfo().getBaseUrl();

        // NumeroExpediteur (peut être null)
        if (ud.getInfobipInfo().getNumeroExpediteur() != null) {
            dto.idNumero = ud.getInfobipInfo().getNumeroExpediteur().getId();
            dto.valeurNumero = ud.getInfobipInfo().getNumeroExpediteur().getValeur();
            dto.dateCreationNumero = ud.getInfobipInfo().getNumeroExpediteur().getDateCreation();
        }

        // UsersDetail
        dto.dateCreationUsersDetail = ud.getDateCreation();

        return dto;
    }

    // Getters
    public Long getIdUtilisateur() { return idUtilisateur; }
    public String getUsername() { return username; }
    public String getRole() { return role; }
    public LocalDateTime getDateCreationUtilisateur() { return dateCreationUtilisateur; }
    public Long getIdInfobip() { return idInfobip; }
    public String getApiKey() { return apiKey; }
    public String getBaseUrl() { return baseUrl; }
    public Long getIdNumero() { return idNumero; }
    public String getValeurNumero() { return valeurNumero; }
    public LocalDateTime getDateCreationNumero() { return dateCreationNumero; }
    public LocalDateTime getDateCreationUsersDetail() { return dateCreationUsersDetail; }
}
