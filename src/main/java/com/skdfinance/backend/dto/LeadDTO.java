package com.skdfinance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Admin-facing view of a lead (includes status + timestamps that the
 * public submitter never sends).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadDTO {

    private Long id;
    private String fullName;
    private String phone;
    private String email;
    private String serviceInterested;
    private String message;
    private String status;
    private LocalDateTime createdAt;
}
