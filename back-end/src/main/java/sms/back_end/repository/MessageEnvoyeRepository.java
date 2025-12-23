package sms.back_end.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sms.back_end.entity.MessageEnvoye;

public interface MessageEnvoyeRepository extends JpaRepository<MessageEnvoye, Long> {
     // Méthode pour trouver un message envoyé par message + destinataire
    Optional<MessageEnvoye> findByIdMessageAndIdNumeroDestinataire(Long idMessage, Long idNumeroDestinataire);
    Optional<MessageEnvoye> findTopByIdNumeroDestinataireOrderByIdDesc(Long idNumeroDestinataire);

}
