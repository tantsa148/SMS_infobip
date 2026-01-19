package sms.client.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import sms.client.dto.destinataire.NumeroDestinataireRequestDTO;
import sms.client.dto.destinataire.NumeroDestinataireResponseDTO;
@Service
public class NumeroDestinataireService {

    private static final Logger logger = LoggerFactory.getLogger(NumeroDestinataireService.class);

    private final RestTemplate restTemplate;

    public NumeroDestinataireService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NumeroDestinataireResponseDTO ajouterNumero(
            String valeur, int plateformeId, int userId) {

        String url = "http://localhost:8080/api/numeros-destinataire";
        

        logger.info("Préparation de la requête pour créer le numéro: {}", valeur);

        // Construire le corps de la requête
        NumeroDestinataireRequestDTO request = new NumeroDestinataireRequestDTO();
        request.setValeur(valeur);

        NumeroDestinataireRequestDTO.PlateformeDTO plateforme = new NumeroDestinataireRequestDTO.PlateformeDTO();
        plateforme.setId(plateformeId);
        request.setPlateforme(plateforme);

        NumeroDestinataireRequestDTO.UserDTO user = new NumeroDestinataireRequestDTO.UserDTO();
        user.setId(userId);
        request.setUser(user);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<NumeroDestinataireRequestDTO> entity = new HttpEntity<>(request, headers);

        try {
            logger.info("Envoi de la requête POST vers {}", url);
            ResponseEntity<NumeroDestinataireResponseDTO> response =
                    restTemplate.postForEntity(url, entity, NumeroDestinataireResponseDTO.class);

            logger.info("Numéro créé avec succès : {}", response.getBody());
            return response.getBody();

        } catch (HttpClientErrorException e) {
            logger.error("Erreur API lors de la création du numéro: {}", e.getResponseBodyAsString(), e);
            return null;
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la création du numéro", e);
            return null;
        }
    }
public NumeroDestinataireResponseDTO getFirstNumeroByUserId(Long userId, String jwtToken) {

    String url = "http://localhost:8080/api/numeros-destinataire/user/" + userId;

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    // 🔐 Ajouter le token JWT dans l'en-tête
    headers.setBearerAuth(jwtToken);
    // équivalent à : headers.set("Authorization", "Bearer " + jwtToken);

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    try {
        logger.info("Envoi de la requête GET pour récupérer tous les numéros de userId {}", userId);

        ResponseEntity<NumeroDestinataireResponseDTO[]> response =
                restTemplate.exchange(
                        url,
                        org.springframework.http.HttpMethod.GET,
                        entity,
                        NumeroDestinataireResponseDTO[].class
                );

        NumeroDestinataireResponseDTO[] numeros = response.getBody();

        if (numeros != null && numeros.length > 0) {
            NumeroDestinataireResponseDTO premier = numeros[0];
            logger.info("Premier numéro trouvé pour userId {} : {}", userId, premier.getValeur());
            return premier;
        } else {
            logger.warn("Aucun numéro trouvé pour userId {}", userId);
            return null;
        }

    } catch (HttpClientErrorException e) {
        logger.error("Erreur API lors de la récupération des numéros: {}", e.getResponseBodyAsString(), e);
        return null;
    } catch (Exception e) {
        logger.error("Erreur inattendue lors de la récupération des numéros", e);
        return null;
    }
}
public List<NumeroDestinataireResponseDTO> getAllNumeros(String jwtToken) {
    
    String url = "http://localhost:8080/api/numeros-destinataire";

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    headers.setBearerAuth(jwtToken);

    HttpEntity<Void> entity = new HttpEntity<>(headers);

    try {
        logger.info("Envoi de la requête GET pour récupérer tous les numéros destinataires");

        ResponseEntity<NumeroDestinataireResponseDTO[]> response =
                restTemplate.exchange(
                        url,
                        org.springframework.http.HttpMethod.GET,
                        entity,
                        NumeroDestinataireResponseDTO[].class
                );

        NumeroDestinataireResponseDTO[] numerosArray = response.getBody();

        if (numerosArray != null && numerosArray.length > 0) {
            List<NumeroDestinataireResponseDTO> numeros = Arrays.asList(numerosArray);
            logger.info("Nombre de numéros récupérés : {}", numeros.size());
            return numeros;
        } else {
            logger.warn("Aucun numéro destinataire trouvé");
            return Collections.emptyList();
        }

    } catch (HttpClientErrorException e) {
        logger.error("Erreur API lors de la récupération des numéros: {}", e.getResponseBodyAsString(), e);
        return Collections.emptyList();
    } catch (Exception e) {
        logger.error("Erreur inattendue lors de la récupération des numéros", e);
        return Collections.emptyList();
    }
}
}

