package sms.back_end.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
public NumeroExpediteurDTO create(@RequestBody NumeroExpediteurRequest request) {
    NumeroExpediteur numero = new NumeroExpediteur();
    numero.setValeur(request.getValeur()); // valeur obligatoire

    NumeroExpediteur saved = service.createNumero(numero, request.getIdInfobip());
    return new NumeroExpediteurDTO(saved); // renvoie JSON avec id et idInfobip
}


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
