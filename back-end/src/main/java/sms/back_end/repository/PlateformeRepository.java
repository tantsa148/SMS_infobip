package sms.back_end.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sms.back_end.entity.Plateforme;

public interface PlateformeRepository extends JpaRepository<Plateforme, Long> {
    Optional<Plateforme> findByNomPlateforme(String nomPlateforme);
}
