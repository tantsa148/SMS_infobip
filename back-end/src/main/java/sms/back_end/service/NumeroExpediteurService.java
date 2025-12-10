package sms.back_end.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import sms.back_end.entity.InfobipInfo;
import sms.back_end.entity.NumeroExpediteur;
import sms.back_end.exception.BadRequestException;
import sms.back_end.exception.NotFoundException;
import sms.back_end.repository.NumeroExpediteurRepository;

@Service
public class NumeroExpediteurService {

    private final NumeroExpediteurRepository repository;
    private final InfobipInfoService infobipInfoService;
    private final UsersDetailService usersDetailService;  // Injection du service UsersDetailService

    public NumeroExpediteurService(NumeroExpediteurRepository repository, 
                                   InfobipInfoService infobipInfoService,
                                   UsersDetailService usersDetailService) {  // Ajout du paramètre
        this.repository = repository;
        this.infobipInfoService = infobipInfoService;
        this.usersDetailService = usersDetailService;  // Initialisation
    }

    // CREATE
    @Transactional  // Pour garantir l'atomicité
    public NumeroExpediteur createNumero(NumeroExpediteur numero, InfobipInfo infobipInfo, String jwtToken) {
        if (numero.getValeur() == null || numero.getValeur().isBlank()) {
            throw new BadRequestException("Le numéro expéditeur ne peut pas être vide.");
        }

        if (repository.findByValeur(numero.getValeur()).isPresent()) {
            throw new BadRequestException("Ce numéro expéditeur existe déjà.");
        }

        numero.setDateCreation(LocalDateTime.now());

        // Créer l'InfobipInfo si fourni
        if (infobipInfo != null) {
            InfobipInfo createdInfo = infobipInfoService.createInfobipInfo(infobipInfo);
            numero.setInfobipInfo(createdInfo);
        }

        NumeroExpediteur savedNumero = repository.save(numero);  // Sauvegarder et récupérer l'entité avec ID

        // Créer automatiquement le UsersDetail si jwtToken fourni
        if (jwtToken != null && !jwtToken.isBlank()) {
            usersDetailService.createUserDetail(jwtToken, savedNumero.getId());  // Utiliser l'ID généré
        }

        return savedNumero;
    }

    // ... (autres méthodes inchangées)

    // READ ALL
    public List<NumeroExpediteur> getAllNumeros() {
        return repository.findAll();
    }

    // READ BY ID
    public NumeroExpediteur getNumeroById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("Aucun numéro expéditeur trouvé pour l’ID : " + id));
    }

    // READ BY VALEUR
    public NumeroExpediteur getByValeur(String valeur) {
        return repository.findByValeur(valeur)
                .orElseThrow(() ->
                        new NotFoundException("Numéro expéditeur introuvable : " + valeur));
    }

    // UPDATE
    // Modifié : Prend un InfobipInfo à créer/mettre à jour, puis l'associe
    public NumeroExpediteur updateNumero(Long id, NumeroExpediteur updated, InfobipInfo infobipInfo) {
        NumeroExpediteur numero = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Impossible de mettre à jour : ID introuvable " + id));

        numero.setValeur(updated.getValeur());

        // Créer ou associer l'InfobipInfo si fourni
        if (infobipInfo != null) {
            InfobipInfo createdInfo = infobipInfoService.createInfobipInfo(infobipInfo);  // Injection de la méthode
            numero.setInfobipInfo(createdInfo);  // Associer l'InfobipInfo créé
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