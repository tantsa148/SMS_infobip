package sms.back_end.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.entity.NumeroExpediteur;
import sms.back_end.service.NumeroExpediteurService;

@RestController
@RequestMapping("/api/numeros_expediteur")
public class NumeroExpediteurController {

    private final NumeroExpediteurService service;

    public NumeroExpediteurController(NumeroExpediteurService service) {
        this.service = service;
    }

    // ============================
    // CREATE
    // ============================
    @PostMapping
    public NumeroExpediteur createNumero(@RequestBody NumeroExpediteur numero) {
        return service.createNumero(numero);
    }

    // ============================
    // READ ALL
    // ============================
    @GetMapping
    public List<NumeroExpediteur> getAllNumeros() {
        return service.getAllNumeros();
    }

    // ============================
    // READ BY ID
    // ============================
        @GetMapping("/{id}")
        public NumeroExpediteur getNumeroById(@PathVariable Long id) {
            return service.getNumeroById(id);
        }

    // ============================
    // READ BY valeur_numero
    // ============================
    @GetMapping("/valeur/{valeur}")
    public NumeroExpediteur getByValeur(@PathVariable String valeur) {
        return service.getByValeur(valeur);
    }

    // ============================
    // UPDATE
    // ============================
    @PutMapping("/{id}")
    public NumeroExpediteur updateNumero(@PathVariable Long id, @RequestBody NumeroExpediteur updated) {
        return service.updateNumero(id, updated);
    }

    // ============================
    // DELETE
    // ============================
    @DeleteMapping("/{id}")
    public void deleteNumero(@PathVariable Long id) {
        service.deleteNumero(id);
    }
}
