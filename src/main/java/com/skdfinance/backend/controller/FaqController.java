package com.skdfinance.backend.controller;

import com.skdfinance.backend.dto.FaqDTO;
import com.skdfinance.backend.service.FaqService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FaqController {

    private final FaqService faqService;

    @GetMapping("/api/faqs")
    public ResponseEntity<List<FaqDTO>> getPublishedFaqs(@RequestParam(required = false) String category) {
        return ResponseEntity.ok(faqService.getPublishedFaqs(category));
    }

    @GetMapping("/api/admin/faqs")
    public ResponseEntity<List<FaqDTO>> getAllFaqsForAdmin() {
        return ResponseEntity.ok(faqService.getAllFaqsForAdmin());
    }

    @PostMapping("/api/admin/faqs")
    public ResponseEntity<FaqDTO> createFaq(@Valid @RequestBody FaqDTO request) {
        FaqDTO created = faqService.createFaq(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/api/admin/faqs/{id}")
    public ResponseEntity<FaqDTO> updateFaq(@PathVariable Long id, @Valid @RequestBody FaqDTO request) {
        return ResponseEntity.ok(faqService.updateFaq(id, request));
    }

    @PatchMapping("/api/admin/faqs/{id}/publish")
    public ResponseEntity<FaqDTO> setPublished(@PathVariable Long id, @RequestParam boolean value) {
        return ResponseEntity.ok(faqService.setPublished(id, value));
    }

    @DeleteMapping("/api/admin/faqs/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        faqService.deleteFaq(id);
        return ResponseEntity.noContent().build();
    }
}