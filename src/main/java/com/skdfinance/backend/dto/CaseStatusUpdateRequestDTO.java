package com.skdfinance.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin-only. Body for adding one timeline entry to an existing ApplicationCase.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseStatusUpdateRequestDTO {

    @NotBlank(message = "Stage is required")
    private String stage;

    private String note;
}
