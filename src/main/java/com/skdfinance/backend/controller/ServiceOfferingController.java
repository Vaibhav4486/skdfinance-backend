package com.skdfinance.backend.controller;

import com.skdfinance.backend.dto.ServiceOfferingDTO;
import com.skdfinance.backend.service.ServiceOfferingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ServiceOfferingController {

    private final ServiceOfferingService serviceOfferingService;

    @GetMapping("/api/services")
    public ResponseEntity<List<ServiceOfferingDTO>> getAllServices() {
        return ResponseEntity.ok(serviceOfferingService.getAllServices());
    }

    @GetMapping("/api/services/{slug}")
    public ResponseEntity<ServiceOfferingDTO> getServiceBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(serviceOfferingService.getServiceBySlug(slug));
    }

    @PostMapping("/api/admin/services")
    public ResponseEntity<ServiceOfferingDTO> createServiceOffering(@Valid @RequestBody ServiceOfferingDTO request) {
        ServiceOfferingDTO created = serviceOfferingService.createServiceOffering(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/api/admin/services/{id}")
    public ResponseEntity<ServiceOfferingDTO> updateServiceOffering(
            @PathVariable Long id,
            @Valid @RequestBody ServiceOfferingDTO request) {
        return ResponseEntity.ok(serviceOfferingService.updateServiceOffering(id, request));
    }

    @DeleteMapping("/api/admin/services/{id}")
    public ResponseEntity<Void> deleteServiceOffering(@PathVariable Long id) {
        serviceOfferingService.deleteServiceOffering(id);
        return ResponseEntity.noContent().build();
    }
}
