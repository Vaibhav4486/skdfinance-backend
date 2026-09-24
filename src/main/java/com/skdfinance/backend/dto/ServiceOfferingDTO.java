package com.skdfinance.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOfferingDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Slug is required")
    private String slug;

    @NotBlank(message = "Short description is required")
    private String shortDescription;

    private String startingRate;
    private String maxAmount;
    private String maxTenure;
    private String iconKey;

    @NotNull(message = "Display order is required")
    private Integer displayOrder;
}
