package sms.client.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import sms.client.dto.plateforme.PlateformeDTO;

@Service
public class PlateformeService {

    private final RestTemplate restTemplate;

    public PlateformeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<PlateformeDTO> getPlateformes() {
        String url = "http://localhost:8080/api/plateformes";

        ResponseEntity<PlateformeDTO[]> response =
                restTemplate.exchange(url, HttpMethod.GET, null, PlateformeDTO[].class);

        return Arrays.asList(response.getBody());
    }
}
