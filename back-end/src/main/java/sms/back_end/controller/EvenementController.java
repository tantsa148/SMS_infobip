package sms.back_end.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import sms.back_end.service.*;
import sms.back_end.entity.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/evenements")
public class EvenementController {

    @Autowired
    private EvenementService service;

    @PostMapping
    public Evenement create(@RequestBody Evenement evenement) {
        return service.create(evenement);
    }

    @GetMapping
    public List<Evenement> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Evenement> getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping("/code/{code}")
    public Optional<Evenement> getByCode(@PathVariable String code) {
        return service.getByCode(code);
    }

    @PutMapping("/{id}")
    public Evenement update(@PathVariable Long id, @RequestBody Evenement evenement) {
        return service.update(id, evenement);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}

