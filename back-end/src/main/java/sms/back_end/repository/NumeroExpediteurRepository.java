package sms.back_end.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sms.back_end.entity.NumeroExpediteur;

@Repository
public interface NumeroExpediteurRepository extends JpaRepository<NumeroExpediteur, Long> {
    Optional<NumeroExpediteur> findByValeur(String valeur);
}
