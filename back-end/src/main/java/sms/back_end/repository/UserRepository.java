package sms.back_end.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sms.back_end.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
