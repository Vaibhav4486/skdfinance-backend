package com.skdfinance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseStatusUpdateDTO {

    private String stage;
    private String note;
    private LocalDateTime updatedAt;
}
