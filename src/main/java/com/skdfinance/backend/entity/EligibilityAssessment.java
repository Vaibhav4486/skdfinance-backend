package com.skdfinance.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Stores every eligibility-checker submission so an advisor can follow up,
 * even if the visitor never fills the contact form separately.
 */
@Entity
@Table(name = "eligibility_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private BigDecimal monthlyIncome;

    @Column(nullable = false)
    private Integer age;

    @Column(nullable = false)
    private String employmentType;

    private BigDecimal existingEmi;

    private Integer cibilScore;

    private String city;

    @Column(nullable = false)
    private String loanType;

    // ---- computed result, stored for reporting ----
    private BigDecimal eligibleAmount;

    private Integer eligibilityScore;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
