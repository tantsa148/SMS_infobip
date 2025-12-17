package sms.back_end.service;

import java.util.List;

import org.springframework.stereotype.Service;

import sms.back_end.entity.Plateforme;
import sms.back_end.exception.NotFoundException;
import sms.back_end.repository.PlateformeRepository;

@Service
public class PlateformeService {

    private final PlateformeRepository repository;

    public PlateformeService(PlateformeRepository repository) {
        this.repository = repository;
    }

    // ============================
    // CREATE
    // ============================
    public Plateforme createPlateforme(Plateforme plateforme) {
        return repository.save(plateforme);
    }

    // ============================
    // READ ALL
    // ============================
    public List<Plateforme> getAllPlateformes() {
        return repository.findAll();
    }

    // ============================
    // READ BY ID
    // ============================
    public Plateforme getPlateformeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Plateforme ID=" + id + " introuvable"));
    }

    // ============================
    // READ BY NAME
    // ============================
    public Plateforme getPlateformeByNom(String nom) {
        return repository.findByNomPlateforme(nom)
                .orElseThrow(() ->
                        new NotFoundException("Plateforme nom=\"" + nom + "\" introuvable"));
    }

    // ============================
    // UPDATE
    // ============================
    public Plateforme updatePlateforme(Long id, Plateforme updatedPlateforme) {

        Plateforme existing = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Plateforme ID=" + id + " introuvable"));

        // Mise à jour
        existing.setNomPlateforme(updatedPlateforme.getNomPlateforme());

        return repository.save(existing);
    }

    // ============================
    // DELETE
    // ============================
    public void deletePlateforme(Long id) {

        Plateforme existing = repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Impossible de supprimer : Plateforme ID=" + id + " introuvable"));

        repository.delete(existing);
    }
}
