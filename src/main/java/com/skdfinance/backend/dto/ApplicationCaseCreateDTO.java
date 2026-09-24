package com.skdfinance.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin-only. Has phone/email that the public-facing ApplicationCaseDTO
 * deliberately omits — this one never goes back out to an unauthenticated caller.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationCaseCreateDTO {

    @NotBlank(message = "Applicant name is required")
    private String applicantName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String phone;

    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Service type is required")
    private String serviceType;

    private String assignedAdvisor;
}
