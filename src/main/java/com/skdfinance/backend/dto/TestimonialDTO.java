package com.skdfinance.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestimonialDTO {

    private Long id;

    @NotBlank(message = "Client name is required")
    private String clientName;

    private String clientRole;

    @NotBlank(message = "Service used is required")
    private String serviceUsed;

    @NotBlank(message = "Quote text is required")
    private String quote;

    private Integer rating;
    private String location;
    private Boolean approved;
}
