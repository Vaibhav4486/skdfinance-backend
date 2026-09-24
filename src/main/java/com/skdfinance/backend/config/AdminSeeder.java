package com.skdfinance.backend.config;

import com.skdfinance.backend.entity.User;
import com.skdfinance.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Creates the single admin account on first startup, if none exists yet.
 * There is no public "become an admin" endpoint anywhere in this app — this
 * is the only path that ever creates an ADMIN row.
 *
 * Change the bootstrap password immediately after your first real login —
 * there's no password-change endpoint yet, so for now that means updating
 * the users table directly (with a fresh BCrypt hash, never plaintext).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.bootstrap.email}")
    private String adminEmail;

    @Value("${admin.bootstrap.password}")
    private String adminPassword;

    @Value("${admin.bootstrap.full-name}")
    private String adminFullName;

    @Override
    public void run(String... args) {
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        User admin = User.builder()
                .fullName(adminFullName)
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .role("ADMIN")
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);
        log.info("Bootstrap admin account created: {}", adminEmail);
    }
}