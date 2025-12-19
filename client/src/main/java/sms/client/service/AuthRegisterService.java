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
import sms.client.dto.sms.SmsRequest;
import sms.client.dto.user.LoginResponseDTO;
import sms.client.dto.user.RegisterResponseDTO;
import tools.jackson.databind.ObjectMapper;

@Service
public class AuthRegisterService {

    private static final Logger log = LoggerFactory.getLogger(AuthRegisterService.class);

    private final RestTemplate restTemplate;
    private final NumeroDestinataireService numeroService;
    private final SmsClientService smsClientService;
    private final ObjectMapper objectMapper;
    private final AuthLoginService authLoginService;

    public AuthRegisterService(
            RestTemplate restTemplate,
            NumeroDestinataireService numeroService,
            SmsClientService smsClientService,
            ObjectMapper objectMapper,
            AuthLoginService authLoginService) {

        this.restTemplate = restTemplate;
        this.numeroService = numeroService;
        this.smsClientService = smsClientService;
        this.objectMapper = objectMapper;
        this.authLoginService = authLoginService;
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
                    restTemplate.postForEntity(url, request, RegisterResponseDTO.class);
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
     * Crée utilisateur, ajoute numéro et envoie SMS
     */
    public RegisterResponseDTO registerClientAndAddNumero(
            String username,
            String password,
            String valeurNumero,
            int plateformeId,
            Long idNumeroExpediteur, // ✅ numéro expéditeur depuis le formulaire
            Long idMessage,            // ✅ message dynamique
            String controllerName,
            String methodName
    ) {
         log.info("📌 Service appelé depuis {}.{}", controllerName, methodName);
        // 1️⃣ Création utilisateur
        RegisterResponseDTO response = registerClient(username, password);

        if (response == null || !response.isSuccess() || response.getUser() == null) {
            return response;
        }

        // 2️⃣ Login automatique
        LoginResponseDTO loginResponse = authLoginService.login(username, password);

        if (loginResponse == null || !loginResponse.isSuccess()) {
            log.warn("❌ Connexion automatique échouée");
            return response;
        }

        String token = loginResponse.getToken();
        log.info("✅ Token JWT récupéré");

        Long userId = response.getUser().getId();

        // 3️⃣ Création du numéro utilisateur (DESTINATAIRE)
        NumeroDestinataireResponseDTO numeroResponse =
                numeroService.ajouterNumero(valeurNumero, plateformeId, userId.intValue());

        if (numeroResponse == null || numeroResponse.getIdNumero() == null) {
            log.warn("❌ Numéro non créé, SMS non envoyé pour l'utilisateur {}", userId);
            return response;
        }

// Numéro créé pour l'utilisateur = destinataire
Long idNumeroDestinataire = numeroResponse.getIdNumero();


        // 4️⃣ Envoi SMS
        try {
            // SmsRequest
            SmsRequest smsRequest = new SmsRequest();
            smsRequest.setIdNumeroExpediteur(idNumeroExpediteur);   // depuis le formulaire
            smsRequest.setIdNumeroDestinataire(idNumeroDestinataire); // dynamique
            smsRequest.setIdMessage(idMessage); // depuis le formulaire

            log.info("📨 Payload SMS: idNumeroExpediteur={}, idNumeroDestinataire={}, idMessage={}",
                    smsRequest.getIdNumeroExpediteur(),
                    smsRequest.getIdNumeroDestinataire(),
                    smsRequest.getIdMessage());

            // Envoi
            smsClientService.envoyerSms(smsRequest, token);

        } catch (Exception e) {
            log.warn("⚠️ Erreur lors de l'envoi du SMS", e);
        }

        return response;
    }
}
