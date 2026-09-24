package com.skdfinance.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * One timeline entry for an ApplicationCase, e.g.
 * "Under Bank Review — forwarded to HDFC, decision expected in 48h".
 */
@Entity
@Table(name = "case_status_updates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseStatusUpdate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_case_id", nullable = false)
    private ApplicationCase applicationCase;

    @Column(nullable = false)
    private String stage;

    @Column(columnDefinition = "TEXT")
    private String note;

    @Column(nullable = false, updatable = false)
    private LocalDateTime updatedAt;
}
