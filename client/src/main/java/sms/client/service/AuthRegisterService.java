package sms.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import sms.client.dto.destinataire.NumeroDestinataireResponseDTO;
import sms.client.dto.modele.ModeleMessageDTO;
import sms.client.dto.otp.OtpResponseDTO;
import sms.client.dto.otp.OtpSendRequestDTO;
import sms.client.dto.user.LoginResponseDTO;
import sms.client.dto.user.RegisterResponseDTO;
import tools.jackson.databind.ObjectMapper;

@Service
public class AuthRegisterService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthRegisterService.class);

    private final RestTemplate restTemplate;
    private final NumeroDestinataireService numeroService;
    private final ObjectMapper objectMapper;
    private final AuthLoginService authLoginService;
    private final ModeleMessageClientService modeleMessageClientService;
    private final OtpService otpService; // ✅ Injection du service OTP

    public AuthRegisterService(
            RestTemplate restTemplate,
            NumeroDestinataireService numeroService,
            ObjectMapper objectMapper,
            AuthLoginService authLoginService,
            ModeleMessageClientService modeleMessageClientService,
            OtpService otpService) { // ✅ Ajout dans le constructeur

        this.restTemplate = restTemplate;
        this.numeroService = numeroService;
        this.objectMapper = objectMapper;
        this.authLoginService = authLoginService;
        this.modeleMessageClientService = modeleMessageClientService;
        this.otpService = otpService; // ✅ Initialisation
    }

    /**
     * Création utilisateur via API Auth
     */
    public RegisterResponseDTO registerClient(String username, String password) {

        String url = "http://localhost:8080/api/auth/register";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        var body = new java.util.HashMap<String, String>();
        body.put("username", username);
        body.put("password", password);
        body.put("role", "CLIENT");

        HttpEntity<?> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<RegisterResponseDTO> response =
                    restTemplate.postForEntity(
                            url,
                            request,
                            RegisterResponseDTO.class
                    );
            return response.getBody();

        } catch (HttpClientErrorException e) {
            try {
                return objectMapper.readValue(
                        e.getResponseBodyAsString(),
                        RegisterResponseDTO.class
                );
            } catch (Exception ex) {
                RegisterResponseDTO error = new RegisterResponseDTO();
                error.setSuccess(false);
                error.setMessage("Erreur lors de l'inscription");
                return error;
            }
        }
    }

    /**
     * Création utilisateur + numéro + SMS dynamique avec OTP
     */
    public RegisterResponseDTO registerClientAndAddNumero(
        String username,
        String password,
        String valeurNumero,
        int plateformeId,
        String controllerName,
        String methodName
    ) {

        log.info("📌 Service appelé depuis {}.{}",
                controllerName, methodName);

        // 1️⃣ Création utilisateur
        RegisterResponseDTO response =
                registerClient(username, password);

        if (response == null ||
            !response.isSuccess() ||
            response.getUser() == null) {
            return response;
        }

        // 2️⃣ Login automatique
        LoginResponseDTO loginResponse =
                authLoginService.login(username, password);

        if (loginResponse == null || !loginResponse.isSuccess()) {
            log.warn("❌ Connexion automatique échouée");
            return response;
        }

        // ✅ TOKEN DISPONIBLE ICI
        String token = loginResponse.getToken();
        log.info("✅ Token JWT récupéré");

        // 3️⃣ Récupération du modèle APRÈS login
        ModeleMessageDTO modeleMessage =
                modeleMessageClientService.findByMethode(
                        methodName,
                        token
                );

        if (modeleMessage == null) {
            log.warn("❌ Aucun modèle trouvé pour la méthode {}", methodName);
            return response;
        }

        Long userId = response.getUser().getId();

        // 4️⃣ Création du numéro destinataire
        NumeroDestinataireResponseDTO numeroResponse =
                numeroService.ajouterNumero(
                        valeurNumero,
                        plateformeId,
                        userId.intValue()
                );

        if (numeroResponse == null ||
            numeroResponse.getIdNumero() == null) {

            log.warn("❌ Numéro non créé, SMS non envoyé pour l'utilisateur {}",
                    userId);
            return response;
        }

        Long idNumeroDestinataire = numeroResponse.getIdNumero();

        // 5️⃣ Préparation et envoi de l'OTP
        OtpResponseDTO otpResponse = null;
        try {
            // ✅ Construction de la requête OTP avec les données du modèle
            OtpSendRequestDTO otpRequest = new OtpSendRequestDTO();
            otpRequest.setIdMessage(modeleMessage.getIdMessage()); // Du modèle
            otpRequest.setIdNumeroExpediteur(modeleMessage.getIdExpediteur()); // Du modèle
            otpRequest.setIdNumeroDestinataire(idNumeroDestinataire); // Du numéro créé

            log.info("📤 Envoi OTP - Message: {}, Expéditeur: {}, Destinataire: {}",
                    modeleMessage.getIdMessage(),
                    modeleMessage.getIdExpediteur(),
                    idNumeroDestinataire);

            // ✅ Appel du service OTP avec le token JWT
            otpResponse = otpService.envoyerOtp(otpRequest, token);

            if (otpResponse != null && otpResponse.isSuccess()) {
                log.info("✅ OTP envoyé avec succès - ID Message envoyé: {}",
                        otpResponse.getIdMessageEnvoye());
            } else {
                log.warn("⚠️ Échec de l'envoi OTP: {}",
                        otpResponse != null ? otpResponse.getMessage() : "Réponse nulle");
            }

        } catch (Exception e) {
            log.error("❌ Erreur lors de l'envoi de l'OTP pour l'utilisateur {}", userId, e);
            // On continue même si l'OTP échoue (l'utilisateur est créé)
        }

        // 6️⃣ Stocker l'idMessageEnvoye pour la vérification OTP
        if (otpResponse != null && otpResponse.getIdMessageEnvoye() != null) {
            response.setIdMessageEnvoye(otpResponse.getIdMessageEnvoye());
        }

        return response;
    }
}