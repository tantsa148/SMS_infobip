package sms.back_end.service;

import java.util.List;

import org.springframework.stereotype.Service;

import sms.back_end.entity.InfobipInfo;
import sms.back_end.exception.NotFoundException;
import sms.back_end.repository.InfobipInfoRepository;

@Service
public class InfobipInfoService {

    private final InfobipInfoRepository repository;

    public InfobipInfoService(InfobipInfoRepository repository) {
        this.repository = repository;
    }

    // CREATE
    public InfobipInfo createInfobipInfo(InfobipInfo info) {
        // Pas besoin de gérer les numéros ici
        return repository.save(info);
    }

    // READ ALL
    public List<InfobipInfo> getAll() {
        return repository.findAll();
    }

    // READ BY ID
    public InfobipInfo getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("InfobipInfo introuvable pour l'ID : " + id));
    }

    // UPDATE
    public InfobipInfo update(Long id, InfobipInfo updated) {
        InfobipInfo info = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Impossible de mettre à jour : ID introuvable " + id));

        info.setApiKey(updated.getApiKey());
        info.setBaseUrl(updated.getBaseUrl());

        // Pas de gestion des numéros ici
        return repository.save(info);
    }

    // DELETE
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Impossible de supprimer : ID introuvable " + id);
        }
        repository.deleteById(id);
    }
}
