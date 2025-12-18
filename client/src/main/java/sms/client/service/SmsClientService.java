package sms.client.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import sms.client.dto.sms.SmsRequest;

@Service
public class SmsClientService {

    private final WebClient webClient;

    public SmsClientService(WebClient.Builder builder) {
        this.webClient = builder
                .baseUrl("http://localhost:8080")
                .build();
    }

    public String envoyerSms(SmsRequest request, String token) {

    return webClient.post()
            .uri("/api/sms/send")
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", "Bearer " + token) // 🔐 TOKEN ICI
            .bodyValue(request)
            .retrieve()
            .bodyToMono(String.class)
            .block();
}

}
