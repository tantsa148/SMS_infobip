package sms.back_end.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import sms.back_end.entity.NumeroDestinataire;
import sms.back_end.exception.BadRequestException;
import sms.back_end.exception.NotFoundException;
import sms.back_end.repository.NumeroDestinataireRepository;

@Service
public class NumeroDestinataireService {

    private final NumeroDestinataireRepository repository;

    public NumeroDestinataireService(NumeroDestinataireRepository repository) {
        this.repository = repository;
    }

    // ============================
    // CREATE
    // ============================
    public NumeroDestinataire createNumero(NumeroDestinataire numero) {

        if (numero.getValeur() == null || numero.getValeur().isBlank()) {
            throw new BadRequestException("Le numéro destinataire ne peut pas être vide.");
        }

        if (repository.findByValeur(numero.getValeur()).isPresent()) {
            throw new BadRequestException("Ce numéro destinataire existe déjà.");
        }

        numero.setDateCreation(LocalDateTime.now());
        return repository.save(numero);
    }

    // ============================
    // READ ALL
    // ============================
    public List<NumeroDestinataire> getAllNumeros() {
        return repository.findAll();
    }

    // ============================
    // READ BY ID
    // ============================
    public NumeroDestinataire getNumeroById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Aucun numéro destinataire trouvé pour l’ID : " + id));
    }

    // ============================
    // READ BY VALEUR
    // ============================
    public NumeroDestinataire getByValeur(String valeur) {
        return repository.findByValeur(valeur)
                .orElseThrow(() ->
                        new NotFoundException("Numéro destinataire introuvable : " + valeur));
    }

    // ============================
    // UPDATE
    // ============================
    public NumeroDestinataire updateNumero(Long id, NumeroDestinataire updated) {

        NumeroDestinataire numero = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Impossible de mettre à jour : ID introuvable " + id));

        numero.setValeur(updated.getValeur());

        return repository.save(numero);
    }

    // ============================
    // DELETE
    // ============================
    public void deleteNumero(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Impossible de supprimer : ID introuvable " + id);
        }
        repository.deleteById(id);
    }
}
