package com.skdfinance.backend.controller;

import com.skdfinance.backend.dto.LeadDTO;
import com.skdfinance.backend.dto.LeadRequestDTO;
import com.skdfinance.backend.service.LeadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LeadController {

    private final LeadService leadService;

    @PostMapping("/api/leads")
    public ResponseEntity<LeadDTO> createLead(@Valid @RequestBody LeadRequestDTO request) {
        LeadDTO created = leadService.createLead(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/api/admin/leads")
    public ResponseEntity<List<LeadDTO>> getAllLeads(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(leadService.getAllLeads(status));
    }

    // query param here rather than a one-field request DTO — not worth a class for a single value
    @PatchMapping("/api/admin/leads/{id}/status")
    public ResponseEntity<LeadDTO> updateLeadStatus(@PathVariable Long id, @RequestParam String value) {
        return ResponseEntity.ok(leadService.updateLeadStatus(id, value));
    }
}
