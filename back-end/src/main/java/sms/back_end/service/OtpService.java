package sms.back_end.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.back_end.dto.SmsRequestDTO;
import sms.back_end.entity.MessageEnvoye;
import sms.back_end.entity.Otp;
import sms.back_end.entity.SmsMessage;
import sms.back_end.repository.MessageEnvoyeRepository;
import sms.back_end.repository.OtpRepository;
import sms.back_end.util.OtpUtil;

@Service
public class OtpService {

    private final InfobipSmsService infobipSmsService;
    private final MessageEnvoyeRepository messageEnvoyeRepo;
    private final OtpRepository otpRepo;
    private final SmsMessageService smsMessageService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public OtpService(InfobipSmsService infobipSmsService,
                      MessageEnvoyeRepository messageEnvoyeRepo,
                      OtpRepository otpRepo,
                      SmsMessageService smsMessageService) {
        this.infobipSmsService = infobipSmsService;
        this.messageEnvoyeRepo = messageEnvoyeRepo;
        this.otpRepo = otpRepo;
        this.smsMessageService = smsMessageService;
    }

    /**
     * Génère un OTP, l'injecte dans le message existant et l'envoie par SMS.
     * Ne crée PAS de nouveau message en base.
     */
    @Transactional
    public void generateAndSendOtp(Long idNumeroExpediteur,
                                   Long idNumeroDestinataire,
                                   Long idMessage) {

        // 1️⃣ Génération OTP
        String otpCode = OtpUtil.generateOtp(6);
        String otpHash = passwordEncoder.encode(otpCode);

        // 2️⃣ Charger le template existant
        SmsMessage templateMessage = smsMessageService.getMessageById(idMessage)
                .orElseThrow(() -> new RuntimeException("Template OTP introuvable"));

        // 3️⃣ Remplacer {{code}} par l’OTP
        String texteFinal = templateMessage.getTexte().replace("{{code}}", otpCode);

        // 4️⃣ Préparer la requête SMS
        SmsRequestDTO dto = new SmsRequestDTO();
        dto.setIdNumeroExpediteur(idNumeroExpediteur);
        dto.setIdNumeroDestinataire(idNumeroDestinataire);
        dto.setIdMessage(idMessage);
        dto.setMessage(texteFinal); // 🔥 OTP injecté ici

        // 5️⃣ Envoi SMS
        infobipSmsService.sendSms(dto);

        // 6️⃣ Récupérer le DERNIER message envoyé à ce numéro
        MessageEnvoye messageEnvoye = messageEnvoyeRepo
                .findTopByIdNumeroDestinataireOrderByIdDesc(idNumeroDestinataire).orElseThrow(() -> new RuntimeException("MessageEnvoye introuvable"));

        // 7️⃣ Sauvegarder OTP
        Otp otp = new Otp();
        otp.setIdMessageEnvoye(messageEnvoye.getId());
        otp.setCodeHash(otpHash);
        otp.setStatut("PENDING");
        otp.setDateExpiration(LocalDateTime.now().plusMinutes(5));

        otpRepo.save(otp);
    }

    /**
     * Vérifie un OTP.
     */
    @Transactional
    public boolean verifyOtp(Long idMessageEnvoye, String code) {

        Otp otp = otpRepo.findByIdMessageEnvoye(idMessageEnvoye)
                .orElseThrow(() -> new RuntimeException("OTP introuvable"));

        if (!"PENDING".equals(otp.getStatut())) {
            throw new RuntimeException("OTP déjà utilisé ou expiré");
        }

        if (otp.getDateExpiration().isBefore(LocalDateTime.now())) {
            otp.setStatut("EXPIRED");
            otpRepo.save(otp);
            throw new RuntimeException("OTP expiré");
        }

        if (!passwordEncoder.matches(code, otp.getCodeHash())) {
            throw new RuntimeException("Code OTP incorrect");
        }

        otp.setStatut("VERIFIED");
        otpRepo.save(otp);

        return true;
    }
}
