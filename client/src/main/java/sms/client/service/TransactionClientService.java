package sms.client.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sms.client.dto.transaction.TransactionRequestDTO;
import sms.client.dto.transaction.TransactionResponseDTO;

@Service
public class TransactionClientService {

    private final RestTemplate restTemplate;

    private static final String TRANSACTION_API_URL =
            "http://localhost:8080/api/transactions/envoyer-sms";

    public TransactionClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public TransactionResponseDTO envoyerTransaction(
            TransactionRequestDTO requestDTO,
            String jwtToken
    ) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 🔐 AJOUT DU TOKEN JWT
        headers.setBearerAuth(jwtToken);
        // équivalent à :
        // headers.set("Authorization", "Bearer " + jwtToken);

        HttpEntity<TransactionRequestDTO> request =
                new HttpEntity<>(requestDTO, headers);

        ResponseEntity<TransactionResponseDTO> response =
                restTemplate.postForEntity(
                        TRANSACTION_API_URL,
                        request,
                        TransactionResponseDTO.class
                );

        return response.getBody();
    }
}

