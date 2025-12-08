package sms.back_end.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import sms.back_end.entity.NumeroExpediteur;
import sms.back_end.exception.BadRequestException;
import sms.back_end.exception.NotFoundException;
import sms.back_end.repository.NumeroExpediteurRepository;

@Service
public class NumeroExpediteurService {

    private final NumeroExpediteurRepository repository;

    public NumeroExpediteurService(NumeroExpediteurRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public NumeroExpediteur createNumero(NumeroExpediteur numero) {

        if (numero.getValeur() == null || numero.getValeur().isBlank()) {
            throw new BadRequestException("Le numéro expéditeur ne peut pas être vide.");
        }

        if (repository.findByValeur(numero.getValeur()).isPresent()) {
            throw new BadRequestException("Ce numéro expéditeur existe déjà.");
        }

        numero.setDateCreation(LocalDateTime.now());
        return repository.save(numero);
    }

    // READ ALL
    public List<NumeroExpediteur> getAllNumeros() {
        return repository.findAll();
    }

    // READ BY ID
    public NumeroExpediteur getNumeroById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Aucun numéro expéditeur trouvé pour l’ID : " + id));
    }

    // READ BY VALEUR
    public NumeroExpediteur getByValeur(String valeur) {
        return repository.findByValeur(valeur)
                .orElseThrow(() ->
                        new NotFoundException("Numéro expéditeur introuvable : " + valeur));
    }

    // UPDATE
    public NumeroExpediteur updateNumero(Long id, NumeroExpediteur updated) {

        NumeroExpediteur numero = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Impossible de mettre à jour : ID introuvable " + id));

        numero.setValeur(updated.getValeur());

        return repository.save(numero);
    }

    // DELETE
    public void deleteNumero(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Impossible de supprimer : ID introuvable " + id);
        }
        repository.deleteById(id);
    }
}
