package sms.back_end.service;

import org.springframework.stereotype.Service;

import sms.back_end.dto.InfobipResponseDTO;
import sms.back_end.dto.SendSmsByIdNumeroDTO;
import sms.back_end.dto.SmsFromUserDetailDTO;
import sms.back_end.dto.SmsResponseDTO;
import sms.back_end.entity.MessageEnvoye;
import sms.back_end.entity.NumeroDestinataire;
import sms.back_end.entity.UsersDetail;
import sms.back_end.exception.NotFoundException;
import sms.back_end.repository.MessageEnvoyeRepository;
import sms.back_end.repository.NumeroDestinataireRepository;
import sms.back_end.repository.UsersDetailRepository;


@Service
public class SmsSenderService {

    private final UsersDetailRepository usersDetailRepo;
    private final NumeroDestinataireRepository destinataireRepo;
    private final InfobipSmsService infobipSmsService;
    private final SmsMessageService smsMessageService;
    private final MessageEnvoyeRepository messageEnvoyeRepo;


public SmsSenderService(UsersDetailRepository usersDetailRepo,
                        NumeroDestinataireRepository destinataireRepo,
                        InfobipSmsService infobipSmsService,
                        SmsMessageService smsMessageService,
                        MessageEnvoyeRepository messageEnvoyeRepo) {

    this.usersDetailRepo = usersDetailRepo;
    this.destinataireRepo = destinataireRepo;
    this.infobipSmsService = infobipSmsService;
    this.smsMessageService = smsMessageService;
    this.messageEnvoyeRepo = messageEnvoyeRepo;
}

public SmsResponseDTO sendSmsByDestinataireId(SendSmsByIdNumeroDTO dto) {

    // 1. Récupérer le destinataire
    NumeroDestinataire destinataire = destinataireRepo.findById(dto.getIdDestinataire())
            .orElseThrow(() -> new NotFoundException("Destinataire introuvable"));

    // 2. Récupérer le numéro expéditeur
    UsersDetail ud = usersDetailRepo
            .findByInfobipInfo_NumeroExpediteur_Id(dto.getIdNumero())
            .orElseThrow(() -> new NotFoundException("Numéro expéditeur introuvable"));

    // 3. Récupérer ou créer le message
    String texteMessage;
    Long idMessageFinal;

    if (dto.getIdMessage() != null) {
        // Cas: on reçoit un ID
        var msg = smsMessageService.getMessageById(dto.getIdMessage())
                .orElseThrow(() -> new NotFoundException("Message introuvable"));

        texteMessage = msg.getTexte();
        idMessageFinal = msg.getId();
    } else {
        throw new RuntimeException("idMessage est obligatoire");
    }

    // 4. Préparer le DTO Infobip
    SmsFromUserDetailDTO smsDTO = new SmsFromUserDetailDTO();
    smsDTO.setApiKey(ud.getInfobipInfo().getApiKey());
    smsDTO.setBaseUrl(ud.getInfobipInfo().getBaseUrl());
    smsDTO.setFrom(ud.getInfobipInfo().getNumeroExpediteur().getValeur());
    smsDTO.setTo(destinataire.getValeur());
    smsDTO.setMessage(texteMessage);

    // 5. Envoyer le SMS
    InfobipResponseDTO infobipResp = infobipSmsService.sendSmsFromDto(smsDTO);
        String infobipMessageId = null;
        if (infobipResp != null 
                && infobipResp.getMessageId() != null) {
        infobipMessageId = infobipResp.getMessageId();
        }


        MessageEnvoye msg = new MessageEnvoye();
        msg.setIdNumeroExpediteur(dto.getIdNumero());
        msg.setIdNumeroDestinataire(dto.getIdDestinataire());
        msg.setIdMessage(idMessageFinal);
        msg.setInfobipMessageId(infobipMessageId);

        messageEnvoyeRepo.save(msg);


    // 6. Retourner la réponse enrichie
    return new SmsResponseDTO(
            dto.getIdNumero(),
            dto.getIdDestinataire(),
            idMessageFinal, // ✅ id message réel
            smsDTO.getFrom(),
            smsDTO.getTo(),
            texteMessage,
            smsDTO.getBaseUrl(),
            smsDTO.getApiKey(),
            infobipResp
    );

}
}
