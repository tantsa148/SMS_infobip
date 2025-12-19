package sms.back_end.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import sms.back_end.entity.ModeleMessage;

@Repository
public interface ModeleMessageRepository extends JpaRepository<ModeleMessage, Long> {
}
