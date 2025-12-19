package sms.back_end.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "modele_message")
public class ModeleMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String methode;

    // FK vers numero_expediteur
    @ManyToOne
    @JoinColumn(name = "id_expediteur", nullable = false)
    private NumeroExpediteur expediteur;

    // FK vers messages
    @ManyToOne
    @JoinColumn(name = "id_message", nullable = false)
    private SmsMessage message;

    // =======================
    // Getters & Setters
    // =======================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMethode() { return methode; }
    public void setMethode(String methode) { this.methode = methode; }

    public NumeroExpediteur getExpediteur() { return expediteur; }
    public void setExpediteur(NumeroExpediteur expediteur) { this.expediteur = expediteur; }

    public SmsMessage getMessage() { return message; }
    public void setMessage(SmsMessage message) { this.message = message; }
}
