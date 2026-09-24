package com.skdfinance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * What the public "check your application status" page returns.
 * Deliberately excludes phone/email so a guessed reference number
 * can't leak someone else's contact details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationCaseDTO {

    // the internal DB id — needed by admins to call
    // POST /api/admin/cases/{id}/status-updates. Harmless to expose (it's
    // just an auto-increment number, not sensitive), unlike phone/email
    // which this DTO deliberately omits.
    private Long id;
    private String referenceNumber;
    private String applicantName;
    private String serviceType;
    private String currentStage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CaseStatusUpdateDTO> statusUpdates;
}