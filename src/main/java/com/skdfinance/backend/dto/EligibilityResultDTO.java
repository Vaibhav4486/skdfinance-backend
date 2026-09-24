package com.skdfinance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityResultDTO {

    private Integer eligibilityScore;
    private BigDecimal eligibleAmount;
    private String recommendation;
    private List<String> qualifiesFor;
}
