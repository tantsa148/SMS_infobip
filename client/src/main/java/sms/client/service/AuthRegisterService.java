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
    private final AuthLoginService authLoginService;

    private final RestTemplate restTemplate;
    private final NumeroDestinataireService numeroService;
    private final SmsClientService smsClientService;
    private final ObjectMapper objectMapper;

    public AuthRegisterService(RestTemplate restTemplate,
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
     * Crée un utilisateur via l'API Auth
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
     * Crée un utilisateur, ajoute un numéro et envoie un SMS (non bloquant)
     */
    public RegisterResponseDTO registerClientAndAddNumero(
            String username,
            String password,
            String valeurNumero,
            int plateformeId) {

        // 1️⃣ Créer l'utilisateur
        RegisterResponseDTO response = registerClient(username, password);

        if (response == null || !response.isSuccess() || response.getUser() == null) {
            return response;
        }

        // 🔐 LOGIN
        // 🔐 LOGIN
        LoginResponseDTO loginResponse =
                authLoginService.login(username, password);

        if (loginResponse == null || !loginResponse.isSuccess()) {
            log.warn("❌ Connexion automatique échouée");
            return response; // on stoppe ici
        }

        // ✅ ICI EXACTEMENT
        String token = loginResponse.getToken();

        log.info("✅ Token JWT : {}", token);

        

        Long userId = response.getUser().getId();

        // 2️⃣ Créer le numéro
        NumeroDestinataireResponseDTO numeroResponse =
                numeroService.ajouterNumero(valeurNumero, plateformeId, userId.intValue());

        if (numeroResponse != null && numeroResponse.getIdNumero() != null) {

            Long numeroId = numeroResponse.getIdNumero();

            // 3️⃣ Envoyer le SMS (non bloquant)
            try {
                SmsRequest smsRequest = new SmsRequest(
                        2L,        // idNumeroExpediteur
                        2L,  // idNumeroDestinataire
                        1L         // idMessage
                );

        smsClientService.envoyerSms(smsRequest, token);
            } catch (Exception e) {
                log.warn("⚠️ SMS non envoyé pour le numéro {}", numeroId, e);
            }
        } else {
            log.warn("Numéro non créé, SMS non envoyé pour l'utilisateur {}", userId);
        }

        return response;
    }
}
