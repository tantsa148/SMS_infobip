package sms.back_end.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import sms.back_end.entity.NumeroDestinataire;

public interface NumeroDestinataireRepository extends JpaRepository<NumeroDestinataire, Long> {

    Optional<NumeroDestinataire> findByValeur(String valeurNumero);
    boolean existsByValeur(String valeur);

    @Override
    @EntityGraph(attributePaths = {"plateforme"})
    List<NumeroDestinataire> findAll();

    @EntityGraph(attributePaths = {"plateforme"})
    Optional<NumeroDestinataire> findById(Long id);

    // 🔹 Nouvelle méthode pour trouver par User
    @EntityGraph(attributePaths = {"plateforme"})
    List<NumeroDestinataire> findByUserId(Long userId);
    
    Optional<NumeroDestinataire> findFirstByUserId(Long userId);
}
