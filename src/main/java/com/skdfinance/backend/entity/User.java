package com.skdfinance.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Covers both admin and customer accounts — distinguished by role, not by
 * separate tables. role is a plain String ("ADMIN" or "CUSTOMER"), validated
 * in the service layer that creates users (no enums, matching the rest of
 * this project's convention).
 *
 * There is exactly one ADMIN row, created once at startup by AdminSeeder.
 * No endpoint in this app can create another one — public registration
 * (AuthService.register) always sets role = "CUSTOMER".
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    private String phone;

    @Column(nullable = false)
    private String password; // BCrypt hash — never store or return plaintext

    @Column(nullable = false)
    private String role; // "ADMIN" or "CUSTOMER"

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}