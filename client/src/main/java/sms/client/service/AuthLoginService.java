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

import sms.client.dto.user.LoginResponseDTO;
import tools.jackson.databind.ObjectMapper;

@Service
public class AuthLoginService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AuthLoginService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public LoginResponseDTO login(String username, String password) {
        String url = "http://localhost:8080/api/auth/login";

        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<LoginResponseDTO> response =
                    restTemplate.postForEntity(url, request, LoginResponseDTO.class);
            return response.getBody();
        } catch (HttpClientErrorException e) {
            try {
                return objectMapper.readValue(
                        e.getResponseBodyAsString(),
                        LoginResponseDTO.class
                );
            } catch (Exception ex) {
                LoginResponseDTO error = new LoginResponseDTO();
                error.setSuccess(false);
                error.setMessage("Erreur lors de la connexion");
                return error;
            }
        }
    }
}
