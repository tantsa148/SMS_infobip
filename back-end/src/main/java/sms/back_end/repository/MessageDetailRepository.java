package sms.back_end.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sms.back_end.entity.MessageDetail;

@Repository
public interface MessageDetailRepository extends JpaRepository<MessageDetail, Long> {

    // Récupérer un message par son messageId Infobip
    Optional<MessageDetail> findByMessageId(String messageId);

    // Récupérer tous les messages par id_message_envoye
    List<MessageDetail> findByMessageEnvoyeId(Integer idMessageEnvoye);

    // Récupérer un message unique par id_message_envoye (si relation 1-1)
    Optional<MessageDetail> findByMessageEnvoye_Id(Integer idMessageEnvoye);

    // Récupérer tous les messages envoyés à un destinataire
    List<MessageDetail> findByCurrency(String currency);

    // Récupérer tous les messages avec un statut spécifique
    List<MessageDetail> findByStatusName(String statusName);
}