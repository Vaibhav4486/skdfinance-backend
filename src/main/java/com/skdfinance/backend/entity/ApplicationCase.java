package com.skdfinance.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Replaces the "live bank tracking" idea from the reference design.
 * This is YOUR office's internal case record. An advisor updates
 * currentStage manually; the client looks it up by referenceNumber
 * on a public "check status" page.
 */
@Entity
@Table(name = "application_cases")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationCase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String referenceNumber;

    @Column(nullable = false)
    private String applicantName;

    @Column(nullable = false)
    private String phone;

    private String email;

    @Column(nullable = false)
    private String serviceType;

    // e.g. "Documents Submitted" -> "Under Review" -> "Approved" -> "Disbursed"
    @Column(nullable = false)
    private String currentStage;

    private String assignedAdvisor;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder.Default
    @OneToMany(mappedBy = "applicationCase", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CaseStatusUpdate> statusUpdates = new ArrayList<>();

    // nullable — best-effort link by phone number when an advisor creates the
    // case (see ApplicationCaseService); null if no matching account exists
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}