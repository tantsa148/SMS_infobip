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

import sms.back_end.dto.ModeleMessageDTO;
import sms.back_end.entity.ModeleMessage;
import sms.back_end.service.ModeleMessageService;

@RestController
@RequestMapping("/api/modele-message")
public class ModeleMessageController {

    private final ModeleMessageService service;

    public ModeleMessageController(ModeleMessageService service) {
        this.service = service;
    }

    // CREATE
    @PostMapping
    public ModeleMessage create(@RequestBody ModeleMessage modeleMessage) {
        return service.save(modeleMessage);
    }

    // READ ALL (renvoie DTO)
    @GetMapping
    public List<ModeleMessageDTO> getAll() {
        return service.findAllDTO();
    }

    // READ BY ID (DTO)
    @GetMapping("/{id}")
    public ModeleMessageDTO getById(@PathVariable Long id) {
        return service.findByIdDTO(id).orElse(null);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ModeleMessage update(@PathVariable Long id, @RequestBody ModeleMessage modeleMessage) {
        modeleMessage.setId(id);
        return service.save(modeleMessage);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteById(id);
    }
}
