package sms.back_end.service;

import java.util.List;
import java.util.Optional;

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
    public Optional<Plateforme> getPlateformeById(Long id) {
        return repository.findById(id);
    }

    // ============================
    // READ BY NAME
    // ============================
    public Optional<Plateforme> getPlateformeByNom(String nom) {
        return repository.findByNomPlateforme(nom);
    }

    // ============================
    // GET OR THROW
    // ============================
    public Plateforme getPlateformeOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("La plateforme avec l'ID " + id + " est introuvable"));
    }

    // ============================
    // UPDATE
    // ============================
    public Plateforme updatePlateforme(Long id, Plateforme updatedPlateforme) {
        return repository.findById(id).map(plateforme -> {
            plateforme.setNomPlateforme(updatedPlateforme.getNomPlateforme());
            return repository.save(plateforme);
        }).orElseThrow(() -> new NotFoundException("Plateforme non trouvée"));
    }

    // ============================
    // DELETE
    // ============================
    public void deletePlateforme(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Impossible de supprimer : plateforme ID=" + id + " introuvable");
        }
        repository.deleteById(id);
    }
}
