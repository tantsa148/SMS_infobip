package sms.client.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sms.client.entity.Solde;


public interface SoldeRepository extends JpaRepository<Solde, Long> {

    Optional<Solde> findByUserId(Long userId);
}
