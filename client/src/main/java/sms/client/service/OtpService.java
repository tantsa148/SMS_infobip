package sms.client.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import sms.client.dto.otp.OtpResponseDTO;
import sms.client.dto.otp.OtpSendRequestDTO;
import sms.client.dto.otp.OtpVerifyRequestDTO;

/**
 * Service OTP pour l'envoi et la vérification des codes OTP
 */
@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);

    private final RestTemplate restTemplate;

    public OtpService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Envoi d'un code OTP avec JWT
     * POST http://localhost:8080/api/otp/send
     */
    public OtpResponseDTO envoyerOtp(OtpSendRequestDTO request, String jwtToken) {
        String url = "http://localhost:8080/api/otp/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        HttpEntity<OtpSendRequestDTO> requestEntity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<OtpResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    OtpResponseDTO.class
            );
            OtpResponseDTO responseDTO = response.getBody();
            if (responseDTO != null) {
                log.info("OTP envoyé avec succès, ID message: {}", responseDTO.getIdMessageEnvoye());
            }
            return responseDTO;
        } catch (HttpClientErrorException e) {
            log.error("Erreur lors de l'envoi OTP: {}", e.getMessage());
            OtpResponseDTO errorResponse = new OtpResponseDTO();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Erreur lors de l'envoi du code OTP");
            return errorResponse;
        }
    }

    /**
     * Vérification d'un code OTP avec JWT
     * POST http://localhost:8080/api/otp/verify
     */
    public OtpResponseDTO verifierOtp(OtpVerifyRequestDTO request, String jwtToken) {
        String url = "http://localhost:8080/api/otp/verify";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtToken);

        HttpEntity<OtpVerifyRequestDTO> requestEntity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<OtpResponseDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    OtpResponseDTO.class
            );
            OtpResponseDTO responseDTO = response.getBody();
            if (responseDTO != null) {
                if (responseDTO.isSuccess()) {
                    log.info("OTP vérifié avec succès");
                } else {
                    log.warn("OTP invalide: {}", responseDTO.getMessage());
                }
            }
            return responseDTO;
        } catch (HttpClientErrorException e) {
            log.error("Erreur lors de la vérification OTP: {}", e.getMessage());
            OtpResponseDTO errorResponse = new OtpResponseDTO();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Erreur lors de la vérification du code OTP");
            return errorResponse;
        }
    }

}

