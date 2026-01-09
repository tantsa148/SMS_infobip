package sms.back_end.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sms.back_end.dto.NumeroDestinataireDTO;
import sms.back_end.entity.NumeroDestinataire;
import sms.back_end.exception.BadRequestException;
import sms.back_end.exception.NotFoundException;
import sms.back_end.service.NumeroDestinataireService;

@RestController
@RequestMapping("/api/numeros-destinataire")
public class NumeroDestinataireController {

    private final NumeroDestinataireService service;
    private final Logger logger = LoggerFactory.getLogger(NumeroDestinataireController.class);

    public NumeroDestinataireController(NumeroDestinataireService service) {
        this.service = service;
    }

    // ===================
    // CREATE
    // ===================
    @PostMapping
    public ResponseEntity<?> create(@RequestBody NumeroDestinataire numero) {
        try {
            NumeroDestinataire created = service.createNumero(numero);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new NumeroDestinataireDTO(created));
        } catch (BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur création numéro : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne lors de l'ajout du numéro");
        }
    }

    // ===================
    // READ ALL
    // ===================
    @GetMapping
    public ResponseEntity<List<NumeroDestinataireDTO>> getAll() {
        List<NumeroDestinataireDTO> list = service.getAllNumeros()
                .stream()
                .map(NumeroDestinataireDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    }

    // ===================
    // READ BY ID
    // ===================
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        try {
            NumeroDestinataire numero = service.getNumeroById(id);
            return ResponseEntity.ok(new NumeroDestinataireDTO(numero));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ===================
    // READ BY VALEUR
    // ===================
    @GetMapping("/search")
    public ResponseEntity<?> getByValeur(@RequestParam String valeur) {
        try {
            NumeroDestinataire numero = service.getByValeur(valeur);
            return ResponseEntity.ok(new NumeroDestinataireDTO(numero));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // ===================
    // UPDATE
    // ===================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody NumeroDestinataire numero) {

        try {
            NumeroDestinataire updated = service.updateNumero(id, numero);
            return ResponseEntity.ok(new NumeroDestinataireDTO(updated));
        } catch (NotFoundException | BadRequestException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur update numéro : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne lors de la mise à jour");
        }
    }

    // ===================
    // DELETE
    // ===================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            service.deleteNumero(id);
            return ResponseEntity.ok("Numéro destinataire supprimé avec succès");
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur suppression numéro : ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur interne lors de la suppression");
        }
    }
    // ===================
// READ BY USER ID
// ===================
@GetMapping("/user/{userId}")
public ResponseEntity<?> getByUserId(@PathVariable Long userId) {
    try {
        List<NumeroDestinataireDTO> list = service.getByUser(userId)
                .stream()
                .map(NumeroDestinataireDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(list);
    } catch (Exception e) {
        logger.error("Erreur récupération des numéros par user : ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Erreur interne lors de la récupération des numéros");
    }
}

}
