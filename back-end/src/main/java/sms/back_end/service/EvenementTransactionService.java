package sms.back_end.service;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.back_end.dto.SmsRequestDTO;
import sms.back_end.entity.EvenementTransaction;
import sms.back_end.entity.MessageEnvoye;
import sms.back_end.entity.SmsMessage;
import sms.back_end.repository.EvenementTransactionRepository;
import sms.back_end.repository.MessageEnvoyeRepository;

@Service
public class EvenementTransactionService {

    private final InfobipSmsService infobipSmsService;
    private final MessageEnvoyeRepository messageEnvoyeRepo;
    private final EvenementTransactionRepository evenementTransactionRepo;
    private final SmsMessageService smsMessageService;

    public EvenementTransactionService(InfobipSmsService infobipSmsService,
                                       MessageEnvoyeRepository messageEnvoyeRepo,
                                       EvenementTransactionRepository evenementTransactionRepo,
                                       SmsMessageService smsMessageService) {
        this.infobipSmsService = infobipSmsService;
        this.messageEnvoyeRepo = messageEnvoyeRepo;
        this.evenementTransactionRepo = evenementTransactionRepo;
        this.smsMessageService = smsMessageService;
    }

    /**
     * Envoie un SMS de transaction et enregistre l'événement en base.
     * Injecte dynamiquement les données dans le template si nécessaire.
     */
    @Transactional
    public void envoyerSmsTransaction(Long idNumeroExpediteur,
                                      Long idNumeroDestinataire,
                                      Long idMessage,
                                      String reference,
                                      BigDecimal montant) {

        // 1️⃣ Vérifier que la référence n'existe pas déjà
        if (evenementTransactionRepo.existsByReference(reference)) {
            throw new RuntimeException("Une transaction avec cette référence existe déjà: " + reference);
        }

        // 2️⃣ Charger le template existant
        SmsMessage templateMessage = smsMessageService.getMessageById(idMessage)
                .orElseThrow(() -> new RuntimeException("Template de message introuvable"));

        // 3️⃣ Remplacer les placeholders dans le texte (optionnel)
        String texteFinal = templateMessage.getTexte()
                .replace("{{reference}}", reference)
                .replace("{{montant}}", montant.toString());

        // 4️⃣ Préparer la requête SMS
        SmsRequestDTO dto = new SmsRequestDTO();
        dto.setIdNumeroExpediteur(idNumeroExpediteur);
        dto.setIdNumeroDestinataire(idNumeroDestinataire);
        dto.setIdMessage(idMessage);
        dto.setMessage(texteFinal); // 🔥 Texte personnalisé injecté ici

        // 5️⃣ Envoi SMS
        infobipSmsService.sendSms(dto);

        // 6️⃣ Récupérer le DERNIER message envoyé à ce numéro
        MessageEnvoye messageEnvoye = messageEnvoyeRepo
                .findTopByIdNumeroDestinataireOrderByIdDesc(idNumeroDestinataire)
                .orElseThrow(() -> new RuntimeException("MessageEnvoye introuvable après envoi"));

        // 7️⃣ Créer et sauvegarder l'événement transaction
        EvenementTransaction transaction = new EvenementTransaction();
        transaction.setMessageEnvoye(messageEnvoye);
        transaction.setReference(reference);
        transaction.setMontant(montant);

        evenementTransactionRepo.save(transaction);

        System.out.println("✅ Transaction enregistrée: REF=" + reference + ", MONTANT=" + montant);
    }

    /**
     * Envoie un SMS de transaction avec montant et numero et enregistre l'événement en base.
     * Injecte dynamiquement les données dans le template (placeholder {{montant}} et {{numero}}).
     */
    @Transactional
    public void envoyerSmsTransactionComplet(Long idNumeroExpediteur,
                                             Long idNumeroDestinataire,
                                             Long idMessage,
                                             String reference,
                                             BigDecimal montant,
                                             String numero) {

        // 1️⃣ Vérifier que la référence n'existe pas déjà
        if (evenementTransactionRepo.existsByReference(reference)) {
            throw new RuntimeException("Une transaction avec cette référence existe déjà: " + reference);
        }

        // 2️⃣ Charger le template existant
        SmsMessage templateMessage = smsMessageService.getMessageById(idMessage)
                .orElseThrow(() -> new RuntimeException("Template de message introuvable"));

        // 3️⃣ Remplacer les placeholders dans le texte (montant ET numero)
        String texteFinal = templateMessage.getTexte()
                .replace("{{reference}}", reference)
                .replace("{{montant}}", montant.toString())
                .replace("{{numero}}", numero);

        // 4️⃣ Préparer la requête SMS
        SmsRequestDTO dto = new SmsRequestDTO();
        dto.setIdNumeroExpediteur(idNumeroExpediteur);
        dto.setIdNumeroDestinataire(idNumeroDestinataire);
        dto.setIdMessage(idMessage);
        dto.setMessage(texteFinal);

        // 5️⃣ Envoi SMS
        infobipSmsService.sendSms(dto);

        // 6️⃣ Récupérer le DERNIER message envoyé à ce numéro
        MessageEnvoye messageEnvoye = messageEnvoyeRepo
                .findTopByIdNumeroDestinataireOrderByIdDesc(idNumeroDestinataire)
                .orElseThrow(() -> new RuntimeException("MessageEnvoye introuvable après envoi"));

        // 7️⃣ Créer et sauvegarder l'événement transaction
        EvenementTransaction transaction = new EvenementTransaction();
        transaction.setMessageEnvoye(messageEnvoye);
        transaction.setReference(reference);
        transaction.setMontant(montant);

        evenementTransactionRepo.save(transaction);

        System.out.println("✅ Transaction enregistrée avec numero: REF=" + reference + ", MONTANT=" + montant + ", NUMERO=" + numero);
    }

    /**
     * Récupère une transaction par sa référence
     */
    public EvenementTransaction getTransactionByReference(String reference) {
        return evenementTransactionRepo.findByReference(reference)
                .orElseThrow(() -> new RuntimeException("Transaction introuvable: " + reference));
    }

    /**
     * Vérifie si une référence existe déjà
     */
    public boolean referenceExiste(String reference) {
        return evenementTransactionRepo.existsByReference(reference);
    }
}