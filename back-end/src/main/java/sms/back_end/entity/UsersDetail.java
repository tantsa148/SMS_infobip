package sms.back_end.entity;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "users_detail")
@IdClass(UsersDetailId.class) // Classe pour clé primaire composite
public class UsersDetail {

    @Id
    @Column(name = "id_utilisateur", nullable = false)
    private Long idUtilisateur;

    @Id
    @Column(name = "id_infobip", nullable = false)
    private Long idInfobip; // si tu utilises ID auto-incrémenté (Long)

    @ManyToOne
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "id_infobip", referencedColumnName = "id", insertable = false, updatable = false)
    private InfobipInfo infobipInfo;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    // Getters et setters
    public Long getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public Long getIdInfobip() { return idInfobip; }
    public void setIdInfobip(Long idInfobip) { this.idInfobip = idInfobip; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public InfobipInfo getInfobipInfo() { return infobipInfo; }
    public void setInfobipInfo(InfobipInfo infobipInfo) { this.infobipInfo = infobipInfo; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
