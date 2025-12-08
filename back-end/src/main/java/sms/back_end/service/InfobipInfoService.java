package sms.back_end.service;

import java.util.List;

import org.springframework.stereotype.Service;

import sms.back_end.entity.InfobipInfo;
import sms.back_end.entity.NumeroExpediteur;
import sms.back_end.exception.NotFoundException;
import sms.back_end.repository.InfobipInfoRepository;
import sms.back_end.repository.NumeroExpediteurRepository;

@Service
public class InfobipInfoService {

    private final InfobipInfoRepository repository;
    private final NumeroExpediteurRepository numeroRepo;

    public InfobipInfoService(InfobipInfoRepository repository, NumeroExpediteurRepository numeroRepo) {
        this.repository = repository;
        this.numeroRepo = numeroRepo;
    }

    // CREATE
    public InfobipInfo createInfobipInfo(InfobipInfo info, Long idNumero) {
        if (idNumero != null) {
            NumeroExpediteur numero = numeroRepo.findById(idNumero)
                    .orElseThrow(() -> new NotFoundException("Numéro expéditeur introuvable : " + idNumero));
            info.setNumeroExpediteur(numero);
        }
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
    public InfobipInfo update(Long id, InfobipInfo updated, Long idNumero) {
        InfobipInfo info = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Impossible de mettre à jour : ID introuvable " + id));

        info.setApiKey(updated.getApiKey());
        info.setBaseUrl(updated.getBaseUrl());

        if (idNumero != null) {
            NumeroExpediteur numero = numeroRepo.findById(idNumero)
                    .orElseThrow(() -> new NotFoundException("Numéro expéditeur introuvable : " + idNumero));
            info.setNumeroExpediteur(numero);
        }

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
