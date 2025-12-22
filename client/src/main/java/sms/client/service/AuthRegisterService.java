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
import sms.client.dto.sms.SmsRequest;
import sms.client.dto.user.LoginResponseDTO;
import sms.client.dto.user.RegisterResponseDTO;
import tools.jackson.databind.ObjectMapper;

@Service
public class AuthRegisterService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthRegisterService.class);

    private final RestTemplate restTemplate;
    private final NumeroDestinataireService numeroService;
    private final SmsClientService smsClientService;
    private final ObjectMapper objectMapper;
    private final AuthLoginService authLoginService;
    private final ModeleMessageClientService modeleMessageClientService;

    public AuthRegisterService(
            RestTemplate restTemplate,
            NumeroDestinataireService numeroService,
            SmsClientService smsClientService,
            ObjectMapper objectMapper,
            AuthLoginService authLoginService,
            ModeleMessageClientService modeleMessageClientService) {

        this.restTemplate = restTemplate;
        this.numeroService = numeroService;
        this.smsClientService = smsClientService;
        this.objectMapper = objectMapper;
        this.authLoginService = authLoginService;
        this.modeleMessageClientService = modeleMessageClientService;
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
     * Création utilisateur + numéro + SMS dynamique
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

    Long idNumeroDestinataire =
            numeroResponse.getIdNumero();

    // 5️⃣ Envoi SMS
    SmsRequest smsRequest = new SmsRequest();
    smsRequest.setIdNumeroExpediteur(
            modeleMessage.getIdExpediteur()
    );
    smsRequest.setIdNumeroDestinataire(
            idNumeroDestinataire
    );
    smsRequest.setIdMessage(
            modeleMessage.getIdMessage()
    );

    log.info("📨 SMS [{}] envoyé",
            modeleMessage.getMethode());

    smsClientService.envoyerSms(smsRequest, token);

    return response;
}

}
