package sms.client.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sms.client.dto.user.LoginResponseDTO;
import sms.client.security.JwtUtilsClient;

@Service
public class AuthLoginService {

    private final RestTemplate restTemplate;
    private final JwtUtilsClient jwtUtilsClient; // ✅ injecté

    public AuthLoginService(RestTemplate restTemplate, JwtUtilsClient jwtUtilsClient) {
        this.restTemplate = restTemplate;
        this.jwtUtilsClient = jwtUtilsClient;
    }

    public LoginResponseDTO login(String username, String password) {

        String url = "http://localhost:8080/api/auth/login";

        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<LoginResponseDTO> response =
                restTemplate.postForEntity(url, request, LoginResponseDTO.class);

        LoginResponseDTO dto = response.getBody();

        // 🔐 Extraire l'id depuis le token JWT
        if (dto != null && dto.getToken() != null) {
            Long userId = jwtUtilsClient.getUserIdFromToken(dto.getToken());
            System.out.println("USER ID depuis JWT = " + userId);
        }

        return dto;
    }
}
