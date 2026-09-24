package com.skdfinance.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * What the public contact form / "enquire now" submits.
 * Validate this with @Valid in the controller.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadRequestDTO {

    @NotBlank(message = "Name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String phone;

    @Email(message = "Enter a valid email address")
    private String email;

    @NotBlank(message = "Please select the service you're interested in")
    private String serviceInterested;

    private String message;
}
