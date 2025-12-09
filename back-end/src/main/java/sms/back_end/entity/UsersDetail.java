package sms.back_end.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "users_detail")
@IdClass(UsersDetailId.class) // Clé primaire composite
public class UsersDetail {

    @Id
    @Column(name = "id_utilisateur", nullable = false)
    private Long idUtilisateur;

    @Id
    @Column(name = "id_numero", nullable = false)
    private Long idNumero;

    // Relation vers User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;

    // Relation vers NumeroExpediteur
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_numero", referencedColumnName = "id_numero", insertable = false, updatable = false)
    private NumeroExpediteur numeroExpediteur;

    @Column(name = "date_creation", nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();

    // ============================
    // Constructeurs
    // ============================
    public UsersDetail() {}

    public UsersDetail(Long idUtilisateur, Long idNumero) {
        this.idUtilisateur = idUtilisateur;
        this.idNumero = idNumero;
    }

    // ============================
    // Getters & Setters
    // ============================
    public Long getIdUtilisateur() { return idUtilisateur; }
    public void setIdUtilisateur(Long idUtilisateur) { this.idUtilisateur = idUtilisateur; }

    public Long getIdNumero() { return idNumero; }
    public void setIdNumero(Long idNumero) { this.idNumero = idNumero; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public NumeroExpediteur getNumeroExpediteur() { return numeroExpediteur; }
    public void setNumeroExpediteur(NumeroExpediteur numeroExpediteur) { this.numeroExpediteur = numeroExpediteur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
