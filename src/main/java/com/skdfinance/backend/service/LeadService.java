package com.skdfinance.backend.service;

import com.skdfinance.backend.dto.LeadDTO;
import com.skdfinance.backend.dto.LeadRequestDTO;
import com.skdfinance.backend.entity.Lead;
import com.skdfinance.backend.entity.User;
import com.skdfinance.backend.repository.LeadRepository;
import com.skdfinance.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeadService {

    // status is a plain String on the entity (no enums), so this is the
    // one place that decides which values are actually legal
    private static final Set<String> VALID_STATUSES = Set.of("NEW", "CONTACTED", "CONVERTED", "CLOSED");

    private final LeadRepository leadRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public LeadDTO createLead(LeadRequestDTO request) {
        // fullName/phone/serviceInterested/email shape is already covered by
        // @NotBlank/@Pattern/@Email on LeadRequestDTO via @Valid in the controller

        Lead lead = Lead.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .serviceInterested(request.getServiceInterested())
                .message(request.getMessage())
                .status("NEW")                 // never accept status from the public form
                .createdAt(LocalDateTime.now())
                .user(getCurrentUser().orElse(null)) // null for guests — this endpoint stays public either way
                .build();

        Lead saved = leadRepository.save(lead);
        emailService.sendNewLeadNotification(saved);
        return toDTO(saved);
    }

    /**
     * If the request carried a valid JWT (JwtAuthenticationFilter runs on
     * every request, public or not), this returns that logged-in user so
     * their submission shows up in their own dashboard. Guests get null.
     */
    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        return userRepository.findByEmail(authentication.getName());
    }

    public List<LeadDTO> getAllLeads(String status) {
        List<Lead> leads;

        if (StringUtils.hasText(status)) {
            if (!VALID_STATUSES.contains(status)) {
                throw new IllegalArgumentException("Invalid status: " + status);
            }
            leads = leadRepository.findByStatus(status);
        } else {
            leads = leadRepository.findAll();
        }

        return leads.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public LeadDTO updateLeadStatus(Long id, String newStatus) {
        if (!StringUtils.hasText(newStatus) || !VALID_STATUSES.contains(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        Lead lead = leadRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Lead not found: " + id));

        lead.setStatus(newStatus);
        Lead saved = leadRepository.save(lead);
        return toDTO(saved);
    }

    private LeadDTO toDTO(Lead lead) {
        return LeadDTO.builder()
                .id(lead.getId())
                .fullName(lead.getFullName())
                .phone(lead.getPhone())
                .email(lead.getEmail())
                .serviceInterested(lead.getServiceInterested())
                .message(lead.getMessage())
                .status(lead.getStatus())
                .createdAt(lead.getCreatedAt())
                .build();
    }
}