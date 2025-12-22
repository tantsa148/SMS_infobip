package sms.client.service;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import sms.client.dto.modele.ModeleMessageDTO;

@Service
public class ModeleMessageClientService {

    private final WebClient webClient;

    public ModeleMessageClientService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    /**
     * Récupère tous les modèles avec JWT
     */
    public List<ModeleMessageDTO> getAll(String token) {

        return webClient.get()
                .uri("/api/modele-message")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + token) // 🔐 TOKEN
                .retrieve()
                .bodyToFlux(ModeleMessageDTO.class)
                .collectList()
                .block();
    }

    /**
     * Trouve un modèle par methode
     */
    public ModeleMessageDTO findByMethode(
            String methode,
            String token) {

        return getAll(token).stream()
                .filter(m ->
                        m.getMethode()
                         .equalsIgnoreCase(methode))
                .findFirst()
                .orElse(null);
    }
}
