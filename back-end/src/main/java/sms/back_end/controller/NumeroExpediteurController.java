package sms.back_end.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.Expediteur.NumeroExpediteurDTO;
import sms.back_end.dto.Expediteur.NumeroExpediteurRequest;
import sms.back_end.entity.NumeroExpediteur;
import sms.back_end.service.NumeroExpediteurService;

@RestController
@RequestMapping("/api/numeros-Expediteur")
public class NumeroExpediteurController {

    private final NumeroExpediteurService service;

    public NumeroExpediteurController(NumeroExpediteurService service) {
        this.service = service;
    }

    @PostMapping
    public NumeroExpediteurDTO create(@RequestBody NumeroExpediteurRequest request, 
                                      @RequestHeader(value = "Authorization", required = false) String jwtToken) {
        NumeroExpediteur numero = new NumeroExpediteur();
        numero.setValeur(request.getValeur());

        // Extraire le token JWT de l'en-tête (supprimer "Bearer " si présent)
        String token = (jwtToken != null && jwtToken.startsWith("Bearer ")) ? jwtToken.substring(7) : jwtToken;

        NumeroExpediteur saved = service.createNumero(numero, request.getInfobipInfo(), token);
        return new NumeroExpediteurDTO(saved);
    }

    // ... (autres méthodes inchangées)

    @GetMapping
    public List<NumeroExpediteurDTO> getAll() {
        return service.getAllNumeros().stream()
                      .map(NumeroExpediteurDTO::new)
                      .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public NumeroExpediteurDTO getById(@PathVariable Long id) {
        return new NumeroExpediteurDTO(service.getNumeroById(id));
    }
}