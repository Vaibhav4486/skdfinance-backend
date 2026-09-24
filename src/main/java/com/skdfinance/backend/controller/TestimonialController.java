package com.skdfinance.backend.controller;

import com.skdfinance.backend.dto.TestimonialDTO;
import com.skdfinance.backend.service.TestimonialService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TestimonialController {

    private final TestimonialService testimonialService;

    @GetMapping("/api/testimonials")
    public ResponseEntity<List<TestimonialDTO>> getApprovedTestimonials(
            @RequestParam(required = false) String serviceUsed) {
        return ResponseEntity.ok(testimonialService.getApprovedTestimonials(serviceUsed));
    }

    @PostMapping("/api/testimonials")
    public ResponseEntity<TestimonialDTO> submitTestimonial(@Valid @RequestBody TestimonialDTO request) {
        TestimonialDTO created = testimonialService.submitTestimonial(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PatchMapping("/api/admin/testimonials/{id}/approve")
    public ResponseEntity<TestimonialDTO> approveTestimonial(@PathVariable Long id) {
        return ResponseEntity.ok(testimonialService.approveTestimonial(id));
    }
}