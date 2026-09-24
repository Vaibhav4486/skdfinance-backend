package com.skdfinance.backend.repository;

import com.skdfinance.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // best-effort match used when an admin creates an ApplicationCase for a
    // walk-in customer — links it to their account if the phone matches
    Optional<User> findByPhone(String phone);
}