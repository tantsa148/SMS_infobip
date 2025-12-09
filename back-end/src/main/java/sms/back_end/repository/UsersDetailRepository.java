package sms.back_end.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sms.back_end.entity.UsersDetail;
import sms.back_end.entity.UsersDetailId;

@Repository
public interface UsersDetailRepository extends JpaRepository<UsersDetail, UsersDetailId> {

    List<UsersDetail> findByIdUtilisateur(Long idUtilisateur);

    Optional<UsersDetail> findByIdNumero(Long idNumero);

    Optional<UsersDetail> findByIdUtilisateurAndIdNumero(Long idUtilisateur, Long idNumero);

    Optional<UsersDetail> findByNumeroExpediteur_Id(Long idNumero);
}
