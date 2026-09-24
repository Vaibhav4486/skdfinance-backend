package com.skdfinance.backend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityRequestDTO {

    @NotBlank(message = "Name is required")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String phone;

    @NotNull(message = "Monthly income is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Monthly income must be greater than zero")
    private BigDecimal monthlyIncome;

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Applicant must be at least 18")
    @Max(value = 75, message = "Age must be realistic")
    private Integer age;

    @NotBlank(message = "Employment type is required")
    private String employmentType;

    @DecimalMin(value = "0.0", message = "Existing EMI cannot be negative")
    private BigDecimal existingEmi;

    @Min(value = 300, message = "CIBIL score must be between 300 and 900")
    @Max(value = 900, message = "CIBIL score must be between 300 and 900")
    private Integer cibilScore;

    private String city;

    @NotBlank(message = "Please select a loan type")
    private String loanType;
}
