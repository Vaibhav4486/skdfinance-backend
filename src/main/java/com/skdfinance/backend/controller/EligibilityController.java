package com.skdfinance.backend.controller;

import com.skdfinance.backend.dto.EligibilityRequestDTO;
import com.skdfinance.backend.dto.EligibilityResultDTO;
import com.skdfinance.backend.service.EligibilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EligibilityController {

    private final EligibilityService eligibilityService;

    // 200, not 201 — the caller gets back a computed result, not a resource
    // they'll fetch again later by id
    @PostMapping("/api/eligibility/check")
    public ResponseEntity<EligibilityResultDTO> checkEligibility(@Valid @RequestBody EligibilityRequestDTO request) {
        return ResponseEntity.ok(eligibilityService.checkEligibility(request));
    }
}
