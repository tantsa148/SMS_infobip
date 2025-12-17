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

import sms.back_end.entity.Plateforme;
import sms.back_end.service.PlateformeService;

@RestController
@RequestMapping("/api/plateformes")
public class PlateformeController {

    private final PlateformeService service;

    public PlateformeController(PlateformeService service) {
        this.service = service;
    }

    // ============================
    // CREATE
    // ============================
    @PostMapping
    public Plateforme createPlateforme(@RequestBody Plateforme plateforme) {
        return service.createPlateforme(plateforme);
    }

    // ============================
    // READ ALL
    // ============================
    @GetMapping
    public List<Plateforme> getAllPlateformes() {
        return service.getAllPlateformes();
    }

    // ============================
    // READ BY ID
    // ============================
    @GetMapping("/{id}")
    public Plateforme getPlateformeById(@PathVariable Long id) {
        return service.getPlateformeById(id);  // plus d'Optional ici
    }

    // ============================
    // READ BY NAME
    // ============================
    @GetMapping("/nom/{nom}")
    public Plateforme getPlateformeByNom(@PathVariable String nom) {
        return service.getPlateformeByNom(nom);
    }

    // ============================
    // UPDATE
    // ============================
    @PutMapping("/{id}")
    public Plateforme updatePlateforme(@PathVariable Long id,
                                       @RequestBody Plateforme plateforme) {
        return service.updatePlateforme(id, plateforme);
    }

    // ============================
    // DELETE
    // ============================
    @DeleteMapping("/{id}")
    public void deletePlateforme(@PathVariable Long id) {
        service.deletePlateforme(id);
    }
}
