package sms.back_end.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.Expediteur.NumeroExpediteurDTO;
import sms.back_end.dto.Expediteur.NumeroExpediteurRequest;
import sms.back_end.entity.NumeroExpediteur;
import sms.back_end.service.NumeroExpediteurService;

@RestController
@RequestMapping("/api/numeros-expediteur")
public class NumeroExpediteurController {

    private final NumeroExpediteurService service;

    public NumeroExpediteurController(NumeroExpediteurService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public NumeroExpediteurDTO create(
            @RequestBody NumeroExpediteurRequest request,
            @RequestHeader(value = "Authorization", required = false) String jwtToken) {

        NumeroExpediteur numero = new NumeroExpediteur();
        numero.setValeur(request.getValeur());

        // Extraire JWT si "Bearer "
        String token = (jwtToken != null && jwtToken.startsWith("Bearer "))
                ? jwtToken.substring(7)
                : jwtToken;

        NumeroExpediteur saved = service.createNumero(
                numero,
                request.getInfobipInfo(),
                request.getIdPlateforme(),
                token
        );

        return new NumeroExpediteurDTO(saved);
    }

    // READ ALL
    @GetMapping
    public List<NumeroExpediteurDTO> getAll() {
        return service.getAllNumeros().stream()
                      .map(NumeroExpediteurDTO::new)
                      .collect(Collectors.toList());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public NumeroExpediteurDTO getById(@PathVariable Long id) {
        return new NumeroExpediteurDTO(service.getNumeroById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public NumeroExpediteurDTO update(
            @PathVariable Long id,
            @RequestBody NumeroExpediteurRequest request) {

        NumeroExpediteur updated = new NumeroExpediteur();
        updated.setValeur(request.getValeur());

        NumeroExpediteur saved = service.updateNumero(
                id,
                updated,
                request.getInfobipInfo(),
                request.getIdPlateforme()
        );

        return new NumeroExpediteurDTO(saved);
    }
}
