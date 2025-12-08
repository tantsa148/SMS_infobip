package sms.back_end.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sms.back_end.entity.UsersDetail;
import sms.back_end.entity.UsersDetailId;

@Repository
public interface UsersDetailRepository extends JpaRepository<UsersDetail, UsersDetailId> {

    // Récupérer tous les détails d’un utilisateur
    List<UsersDetail> findByIdUtilisateur(Long idUtilisateur);

    // Récupérer tous les détails pour un InfobipInfo
    List<UsersDetail> findByIdInfobip(Long idInfobip);
     List<UsersDetail> findByUserId(Long userId);
      Optional<UsersDetail> findByInfobipInfo_NumeroExpediteur_Id(Long idNumero);
}
