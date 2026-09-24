package com.skdfinance.backend.controller;

import com.skdfinance.backend.dto.ApplicationCaseCreateDTO;
import com.skdfinance.backend.dto.ApplicationCaseDTO;
import com.skdfinance.backend.dto.CaseStatusUpdateRequestDTO;
import com.skdfinance.backend.service.ApplicationCaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ApplicationCaseController {

    private final ApplicationCaseService applicationCaseService;

    @GetMapping("/api/cases/{referenceNumber}")
    public ResponseEntity<ApplicationCaseDTO> getCaseByReferenceNumber(@PathVariable String referenceNumber) {
        return ResponseEntity.ok(applicationCaseService.getCaseByReferenceNumber(referenceNumber));
    }

    @PostMapping("/api/admin/cases")
    public ResponseEntity<ApplicationCaseDTO> createCase(@Valid @RequestBody ApplicationCaseCreateDTO request) {
        ApplicationCaseDTO created = applicationCaseService.createCase(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/api/admin/cases/{id}/status-updates")
    public ResponseEntity<ApplicationCaseDTO> addStatusUpdate(
            @PathVariable Long id,
            @Valid @RequestBody CaseStatusUpdateRequestDTO request) {
        return ResponseEntity.ok(applicationCaseService.addStatusUpdate(id, request));
    }
}
