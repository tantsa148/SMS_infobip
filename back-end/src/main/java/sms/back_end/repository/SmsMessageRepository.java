package sms.back_end.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sms.back_end.entity.SmsMessage;

@Repository
public interface SmsMessageRepository extends JpaRepository<SmsMessage, Long> {
    
    // Ajoutez cette méthode pour chercher par texte
    Optional<SmsMessage> findByTexte(String texte);
    
    // Optionnel: chercher par texte exact (case-sensitive)
    Optional<SmsMessage> findByTexteIgnoreCase(String texte);
    
    // Optionnel: vérifier l'existence par texte
    boolean existsByTexte(String texte);
}