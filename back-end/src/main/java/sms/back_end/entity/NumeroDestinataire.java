package sms.back_end.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "numero_destinataire")
public class NumeroDestinataire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNumero;

    @Column(name = "valeur_numero", length = 50, nullable = false, unique = true)
    private String valeur;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    // Relation Many-to-One avec Plateforme
    @ManyToOne(fetch = FetchType.LAZY)  
    @JoinColumn(name = "id_plateforme") 
    private Plateforme plateforme;

    // Relation Many-to-One avec User
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user") // clé étrangère vers users.id
    private User user;

    public NumeroDestinataire() {
        this.dateCreation = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getIdNumero() { return idNumero; }
    public void setIdNumero(Long idNumero) { this.idNumero = idNumero; }

    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public Plateforme getPlateforme() { return plateforme; }
    public void setPlateforme(Plateforme plateforme) { this.plateforme = plateforme; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
