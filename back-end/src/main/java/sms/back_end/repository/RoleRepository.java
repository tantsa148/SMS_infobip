package sms.back_end.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import sms.back_end.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRole(String role);
}
