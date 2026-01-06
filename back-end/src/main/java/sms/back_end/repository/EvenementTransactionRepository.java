package sms.back_end.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sms.back_end.entity.EvenementTransaction;

@Repository
public interface EvenementTransactionRepository extends JpaRepository<EvenementTransaction, Long> {
    
    Optional<EvenementTransaction> findByReference(String reference);
    
    // ✅ CORRECT : utilise "messageEnvoye" qui existe dans l'entité
    List<EvenementTransaction> findByMessageEnvoye_Id(Long messageEnvoyeId);
    
    boolean existsByReference(String reference);
}