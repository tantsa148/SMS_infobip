package sms.back_end.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import sms.back_end.entity.NumeroDestinataire;
import sms.back_end.entity.Plateforme;
import sms.back_end.entity.User;
import sms.back_end.exception.BadRequestException;
import sms.back_end.exception.NotFoundException;
import sms.back_end.repository.NumeroDestinataireRepository;
import sms.back_end.repository.PlateformeRepository;
import sms.back_end.repository.UserRepository;


@Service
public class NumeroDestinataireService {

    private final NumeroDestinataireRepository repository;
    private final PlateformeRepository plateformeRepository;
    private final UserRepository userRepository; // nouveau

    public NumeroDestinataireService(
        NumeroDestinataireRepository repository,
        PlateformeRepository plateformeRepository,
        UserRepository userRepository
    ) {
        this.repository = repository;
        this.plateformeRepository = plateformeRepository;
        this.userRepository = userRepository;
    }

    public NumeroDestinataire createNumero(NumeroDestinataire numero) {
        if (numero.getValeur() == null || numero.getValeur().isBlank())
            throw new BadRequestException("Le numéro destinataire ne peut pas être vide.");

        if (repository.findByValeur(numero.getValeur()).isPresent())
            throw new BadRequestException("Ce numéro destinataire existe déjà.");

        if (numero.getPlateforme() != null) {
            Long idPlateforme = numero.getPlateforme().getId();
            Plateforme plateforme = plateformeRepository.findById(idPlateforme)
                    .orElseThrow(() -> new BadRequestException("Plateforme introuvable : " + idPlateforme));
            numero.setPlateforme(plateforme);
        }

        if (numero.getUser() != null) {
            Long userId = numero.getUser().getId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException("Utilisateur introuvable : " + userId));
            numero.setUser(user);
        }

        numero.setDateCreation(LocalDateTime.now());
        return repository.save(numero);
    }

    public NumeroDestinataire updateNumero(Long id, NumeroDestinataire updated) {
        NumeroDestinataire numero = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("ID introuvable : " + id));

        numero.setValeur(updated.getValeur());

        if (updated.getPlateforme() != null) {
            Long idPlateforme = updated.getPlateforme().getId();
            Plateforme plateforme = plateformeRepository.findById(idPlateforme)
                    .orElseThrow(() -> new BadRequestException("Plateforme introuvable : " + idPlateforme));
            numero.setPlateforme(plateforme);
        } else {
            numero.setPlateforme(null);
        }

        if (updated.getUser() != null) {
            Long userId = updated.getUser().getId();
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new BadRequestException("Utilisateur introuvable : " + userId));
            numero.setUser(user);
        } else {
            numero.setUser(null);
        }

        return repository.save(numero);
    }

    // ============================
    // READ / DELETE restent inchangés
    // ============================
    public List<NumeroDestinataire> getAllNumeros() { return repository.findAll(); }

    public NumeroDestinataire getNumeroById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Aucun numéro destinataire trouvé pour l’ID : " + id));
    }

    public NumeroDestinataire getByValeur(String valeur) {
        return repository.findByValeur(valeur)
                .orElseThrow(() -> new NotFoundException("Numéro destinataire introuvable : " + valeur));
    }

    public void deleteNumero(Long id) {
        if (!repository.existsById(id)) {
            throw new NotFoundException("Impossible de supprimer : ID introuvable " + id);
        }
        repository.deleteById(id);
    }
}
