package sms.back_end.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sms.back_end.entity.Historique;

@Repository
public interface HistoriqueRepository extends JpaRepository<Historique, Long> {
    // ici tu peux ajouter des méthodes custom si besoin
     List<Historique> findByIdUtilisateur(Long idUtilisateur);
}
