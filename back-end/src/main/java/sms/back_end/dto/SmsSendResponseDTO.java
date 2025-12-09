package sms.back_end.dto;

import java.io.Serializable;

import sms.back_end.dto.Infobip.InfobipMessageResponseDTO;

public class SmsSendResponseDTO implements Serializable {

    private Long idNumeroExpediteur;
    private String numeroExpediteur;

    private Long idNumeroDestinataire;
    private String numeroDestinataire;

    private Long idMessage;
    private String message;

    private String statut;
    private String description;

    // Réponse structurée Infobip
    private InfobipMessageResponseDTO infobipResponse;

    // Informations sur le compte Infobip
    private InfobipInfoDTO infobipInfo;

    // Getters & Setters
    public Long getIdNumeroExpediteur() { return idNumeroExpediteur; }
    public void setIdNumeroExpediteur(Long idNumeroExpediteur) { this.idNumeroExpediteur = idNumeroExpediteur; }

    public String getNumeroExpediteur() { return numeroExpediteur; }
    public void setNumeroExpediteur(String numeroExpediteur) { this.numeroExpediteur = numeroExpediteur; }

    public Long getIdNumeroDestinataire() { return idNumeroDestinataire; }
    public void setIdNumeroDestinataire(Long idNumeroDestinataire) { this.idNumeroDestinataire = idNumeroDestinataire; }

    public String getNumeroDestinataire() { return numeroDestinataire; }
    public void setNumeroDestinataire(String numeroDestinataire) { this.numeroDestinataire = numeroDestinataire; }

    public Long getIdMessage() { return idMessage; }
    public void setIdMessage(Long idMessage) { this.idMessage = idMessage; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public InfobipMessageResponseDTO getInfobipResponse() { return infobipResponse; }
    public void setInfobipResponse(InfobipMessageResponseDTO infobipResponse) { this.infobipResponse = infobipResponse; }

    public InfobipInfoDTO getInfobipInfo() { return infobipInfo; }
    public void setInfobipInfo(InfobipInfoDTO infobipInfo) { this.infobipInfo = infobipInfo; }
}
