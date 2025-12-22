package sms.back_end.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import sms.back_end.dto.InfobipBalanceDTO;
import sms.back_end.dto.UsersDetailDTO;

@Service
public class InfobipService {

    private static final Logger log = LoggerFactory.getLogger(InfobipService.class);

    private final UsersDetailService usersDetailService;
    private final WebClient.Builder webClientBuilder;

    public InfobipService(UsersDetailService usersDetailService,
                          WebClient.Builder webClientBuilder) {
        this.usersDetailService = usersDetailService;
        this.webClientBuilder = webClientBuilder;
    }

    /**
     * Récupère le solde Infobip pour un numéro donné.
     * @param jwtToken Le JWT de l'utilisateur
     * @param idNumero L'ID du numéro expéditeur
     * @return InfobipBalanceDTO contenant le solde et la devise
     */
    public InfobipBalanceDTO getBalance(String jwtToken, Long idNumero) {
        try {
            // 1️⃣ Récupérer le UsersDetailDTO
            UsersDetailDTO detail = usersDetailService.getAllUsersDetailForUser(jwtToken)
                    .stream()
                    .filter(d -> d.getIdNumero().equals(idNumero))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Détail utilisateur introuvable pour idNumero=" + idNumero));

            String baseUrl = detail.getBaseUrl();
            String apiKey = detail.getApiKey();

            if (baseUrl == null || apiKey == null) {
                throw new IllegalArgumentException("BaseUrl ou ApiKey manquante pour ce numéro");
            }

            log.info("📡 Appel Infobip : URL={} | API Key=****", baseUrl + "/account/1/balance");

            WebClient client = webClientBuilder.baseUrl(baseUrl).build();

            // 2️⃣ Appel HTTP GET
            InfobipBalanceDTO response = client.get()
                    .uri("/account/1/balance")
                    .header(HttpHeaders.AUTHORIZATION, "App " + apiKey)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(InfobipBalanceDTO.class)
                    .doOnNext(res -> log.info("✅ Réponse Infobip reçue : balance={} {}", res.getBalance(), res.getCurrency()))
                    .block();

            if (response == null) {
                log.warn("⚠️ Réponse Infobip vide pour idNumero={}", idNumero);
                throw new IllegalStateException("Réponse vide d'Infobip");
            }

            return response;

        } catch (WebClientResponseException e) {
            // Erreurs HTTP (401, 403, 500, etc.)
            log.error("❌ Erreur HTTP lors de l'appel Infobip : status={} body={}", e.getRawStatusCode(), e.getResponseBodyAsString(), e);
            throw e;
        } catch (Exception e) {
            // Autres erreurs (NullPointer, blocage, etc.)
            log.error("❌ Exception lors de l'appel Infobip", e);
            throw e;
        }
    }
}
