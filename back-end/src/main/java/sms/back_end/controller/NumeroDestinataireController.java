package sms.back_end.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.entity.NumeroDestinataire;
import sms.back_end.service.NumeroDestinataireService;

@RestController
@RequestMapping("/api/numeros_destinataire")
public class NumeroDestinataireController {

    private final NumeroDestinataireService service;

    public NumeroDestinataireController(NumeroDestinataireService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public NumeroDestinataire create(@RequestBody NumeroDestinataire numero) {
        return service.createNumero(numero);
    }

    // READ ALL
    @GetMapping
    public List<NumeroDestinataire> getAll() {
        return service.getAllNumeros();
    }

    // READ BY ID
    @GetMapping("/{id}")
    public NumeroDestinataire getById(@PathVariable Long id) {
        return service.getNumeroById(id);
    }

    // READ BY VALEUR
    @GetMapping("/search")
    public NumeroDestinataire getByValeur(@RequestParam String valeur) {
        return service.getByValeur(valeur);
    }

    // UPDATE
    @PutMapping("/{id}")
    public NumeroDestinataire update(@PathVariable Long id, @RequestBody NumeroDestinataire numero) {
        return service.updateNumero(id, numero);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteNumero(id);
        return "Numéro destinataire supprimé avec succès";
    }
}
