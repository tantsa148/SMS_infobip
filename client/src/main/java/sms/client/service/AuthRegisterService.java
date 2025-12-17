package sms.client.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import sms.client.dto.user.RegisterResponseDTO;
import tools.jackson.databind.ObjectMapper;

@Service
public class AuthRegisterService {

    private final RestTemplate restTemplate;
    private final NumeroDestinataireService numeroService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthRegisterService(RestTemplate restTemplate,
                               NumeroDestinataireService numeroService) {
        this.restTemplate = restTemplate;
        this.numeroService = numeroService;
    }

    // Méthode de base pour créer un utilisateur
    public RegisterResponseDTO registerClient(String username, String password) {

        String url = "http://localhost:8080/api/auth/register";

        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("role", "CLIENT");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<RegisterResponseDTO> response =
                    restTemplate.postForEntity(url, request, RegisterResponseDTO.class);
            return response.getBody();

        } catch (HttpClientErrorException e) {
            // Cas d'erreur 400 / 409 etc.
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

    // Méthode combinée : création user + numéro
    public RegisterResponseDTO registerClientAndAddNumero(
            String username,
            String password,
            String valeurNumero,
            int plateformeId) {

        // 1️⃣ Créer l'utilisateur
        RegisterResponseDTO response = registerClient(username, password);

        if (response != null && response.isSuccess() && response.getUser() != null) {
            // 2️⃣ Récupérer l'ID généré
            Long userId = response.getUser().getId();

            // 3️⃣ Créer le numéro pour cet utilisateur
            numeroService.ajouterNumero(valeurNumero, plateformeId, userId.intValue());
        }

        return response;
    }
}
