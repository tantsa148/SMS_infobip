package sms.back_end.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sms.back_end.entity.Evenement;
import sms.back_end.repository.EvenementRepository;

@Service
public class EvenementService {

    @Autowired
    private EvenementRepository repository;

    public Evenement create(Evenement evenement) {
        return repository.save(evenement);
    }

    public List<Evenement> getAll() {
        return repository.findAll();
    }

    public Optional<Evenement> getById(Long id) {
        return repository.findById(id);
    }

    public Optional<Evenement> getByCode(String code) {
        return repository.findByCode(code);
    }

    public Evenement update(Long id, Evenement updated) {
        return repository.findById(id).map(e -> {
            e.setCode(updated.getCode());
            e.setDescription(updated.getDescription());
            return repository.save(e);
        }).orElseThrow(() -> new RuntimeException("Événement non trouvé"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}

