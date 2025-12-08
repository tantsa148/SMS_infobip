package sms.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import sms.back_end.entity.MessageEnvoye;

public interface MessageEnvoyeRepository extends JpaRepository<MessageEnvoye, Long> {
}
