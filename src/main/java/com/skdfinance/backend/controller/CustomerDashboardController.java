package com.skdfinance.backend.controller;

import com.skdfinance.backend.dto.CustomerDashboardDTO;
import com.skdfinance.backend.service.CustomerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerDashboardController {

    private final CustomerDashboardService customerDashboardService;

    // SecurityConfig requires authentication on this path — Authentication is
    // guaranteed non-null here, and its name is always the caller's own
    // email (from their token), never anything a client could pass in
    @GetMapping("/api/customer/dashboard")
    public ResponseEntity<CustomerDashboardDTO> getMyDashboard(Authentication authentication) {
        return ResponseEntity.ok(customerDashboardService.getDashboard(authentication.getName()));
    }
}