package sms.back_end.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import sms.back_end.dto.ModeleMessageDTO;
import sms.back_end.entity.ModeleMessage;
import sms.back_end.repository.ModeleMessageRepository;

@Service
public class ModeleMessageService {

    private final ModeleMessageRepository repository;

    public ModeleMessageService(ModeleMessageRepository repository) {
        this.repository = repository;
    }

    // ===== CRUD =====
    public ModeleMessage save(ModeleMessage modeleMessage) {
        return repository.save(modeleMessage);
    }

    public List<ModeleMessageDTO> findAllDTO() {
        return repository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<ModeleMessageDTO> findByIdDTO(Long id) {
        return repository.findById(id).map(this::toDTO);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    // ===== Mapper =====
    private ModeleMessageDTO toDTO(ModeleMessage m) {
        ModeleMessageDTO dto = new ModeleMessageDTO();
        dto.setId(m.getId());
        dto.setMethode(m.getMethode());

        if (m.getExpediteur() != null) {
            dto.setIdExpediteur(m.getExpediteur().getId());
            dto.setValeurExpediteur(m.getExpediteur().getValeur());
        }

        if (m.getMessage() != null) {
            dto.setIdMessage(m.getMessage().getId());
            dto.setTexteMessage(m.getMessage().getTexte());
        }

        return dto;
    }
}
