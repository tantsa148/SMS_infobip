package sms.back_end.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sms.back_end.entity.Evenement;

public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    Optional<Evenement> findByCode(String code);
}

