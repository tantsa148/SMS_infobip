package sms.back_end.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.back_end.entity.InfobipInfo;
import sms.back_end.entity.NumeroExpediteur;
import sms.back_end.entity.Plateforme;
import sms.back_end.exception.BadRequestException;
import sms.back_end.exception.NotFoundException;
import sms.back_end.repository.NumeroExpediteurRepository;

@Service
public class NumeroExpediteurService {

    private final NumeroExpediteurRepository repository;
    private final InfobipInfoService infobipInfoService;
    private final UsersDetailService usersDetailService;
    private final PlateformeService plateformeService;

    public NumeroExpediteurService(NumeroExpediteurRepository repository,
                                   InfobipInfoService infobipInfoService,
                                   UsersDetailService usersDetailService,
                                   PlateformeService plateformeService) {
        this.repository = repository;
        this.infobipInfoService = infobipInfoService;
        this.usersDetailService = usersDetailService;
        this.plateformeService = plateformeService;
    }

    // CREATE
    @Transactional
    public NumeroExpediteur createNumero(NumeroExpediteur numero,
                                         InfobipInfo infobipInfo,
                                         Long idPlateforme,
                                         String jwtToken) {

        if (numero.getValeur() == null || numero.getValeur().isBlank()) {
            throw new BadRequestException("Le numéro expéditeur ne peut pas être vide.");
        }

        // Vérifier si le numéro existe déjà
        Optional<NumeroExpediteur> existingNumero = repository.findByValeur(numero.getValeur());
        if (existingNumero.isPresent()) {
            throw new BadRequestException("Ce numéro expéditeur existe déjà : " + numero.getValeur());
        }

        numero.setDateCreation(LocalDateTime.now());

        if (infobipInfo != null) {
            if (infobipInfo.getId() != null) {
                // Infobip existant
                InfobipInfo existing = infobipInfoService.getById(infobipInfo.getId());
                numero.setInfobipInfo(existing);
            } else {
                // Créer un nouveau Infobip
                InfobipInfo createdInfo = infobipInfoService.createInfobipInfo(infobipInfo);
                numero.setInfobipInfo(createdInfo);
            }
        }

        if (idPlateforme != null) {
            Plateforme plateforme = plateformeService.getPlateformeById(idPlateforme);
            numero.setPlateforme(plateforme);
        }

        NumeroExpediteur saved = repository.save(numero);

        if (jwtToken != null && !jwtToken.isBlank()) {
            usersDetailService.createUserDetail(jwtToken, saved.getId());
        }

        return saved;
    }

    // READ ALL
    public List<NumeroExpediteur> getAllNumeros() {
        return repository.findAll();
    }

    // READ BY ID
    public NumeroExpediteur getNumeroById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aucun numéro expéditeur trouvé pour l’ID : " + id));
    }

    // READ BY VALEUR
    public NumeroExpediteur getByValeur(String valeur) {
        return repository.findByValeur(valeur)
                .orElseThrow(() -> new NotFoundException("Numéro expéditeur introuvable : " + valeur));
    }

    // UPDATE
    @Transactional
    public NumeroExpediteur updateNumero(Long id,
                                         NumeroExpediteur updated,
                                         InfobipInfo infobipInfo,
                                         Long idPlateforme) {

        NumeroExpediteur numero = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Impossible de mettre à jour : ID introuvable " + id));

        if (updated.getValeur() == null || updated.getValeur().isBlank()) {
            throw new BadRequestException("Le numéro expéditeur ne peut pas être vide.");
        }

        // Vérifier si un autre numéro avec la même valeur existe
        Optional<NumeroExpediteur> numeroExist = repository.findByValeur(updated.getValeur());
        if (numeroExist.isPresent() && !numeroExist.get().getId().equals(id)) {
            throw new BadRequestException("Impossible de mettre à jour : ce numéro existe déjà.");
        }

        numero.setValeur(updated.getValeur());

        if (infobipInfo != null) {
            InfobipInfo createdInfo = infobipInfoService.createInfobipInfo(infobipInfo);
            numero.setInfobipInfo(createdInfo);
        }

        if (idPlateforme != null) {
            Plateforme plateforme = plateformeService.getPlateformeById(idPlateforme);
            numero.setPlateforme(plateforme);
        }

        return repository.save(numero);
    }

    // DELETE
    public void deleteNumero(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Impossible de supprimer : ID introuvable " + id);
        }
        repository.deleteById(id);
    }
}
