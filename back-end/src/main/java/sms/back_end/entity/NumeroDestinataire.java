package sms.back_end.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "numero_destinataire")
public class NumeroDestinataire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incrément
    private Long idNumero;

    @Column(name = "valeur_numero", length = 50, nullable = false, unique = true)
    private String valeur;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    public NumeroDestinataire() {
        this.dateCreation = LocalDateTime.now();
    }

    // Getters et setters
    public Long getIdNumero() { return idNumero; }
    public void setIdNumero(Long idNumero) { this.idNumero = idNumero; }

    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }
}
