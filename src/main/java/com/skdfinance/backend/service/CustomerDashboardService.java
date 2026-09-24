package com.skdfinance.backend.service;

import com.skdfinance.backend.dto.ApplicationCaseDTO;
import com.skdfinance.backend.dto.CaseStatusUpdateDTO;
import com.skdfinance.backend.dto.CustomerDashboardDTO;
import com.skdfinance.backend.dto.LeadDTO;
import com.skdfinance.backend.dto.TestimonialDTO;
import com.skdfinance.backend.entity.ApplicationCase;
import com.skdfinance.backend.entity.CaseStatusUpdate;
import com.skdfinance.backend.entity.Lead;
import com.skdfinance.backend.entity.Testimonial;
import com.skdfinance.backend.entity.User;
import com.skdfinance.backend.repository.ApplicationCaseRepository;
import com.skdfinance.backend.repository.LeadRepository;
import com.skdfinance.backend.repository.TestimonialRepository;
import com.skdfinance.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Builds a customer's own view of their history. The identity comes from
 * the authenticated principal (their email, from the JWT) — never from a
 * client-supplied id — so there's no way for one customer to fetch
 * another's dashboard by guessing a parameter.
 */
@Service
@RequiredArgsConstructor
public class CustomerDashboardService {

    private final UserRepository userRepository;
    private final LeadRepository leadRepository;
    private final TestimonialRepository testimonialRepository;
    private final ApplicationCaseRepository applicationCaseRepository;

    public CustomerDashboardDTO getDashboard(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));

        return CustomerDashboardDTO.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .myEnquiries(leadRepository.findByUserId(user.getId())
                        .stream().map(this::toLeadDTO).collect(Collectors.toList()))
                .myTestimonials(testimonialRepository.findByUserId(user.getId())
                        .stream().map(this::toTestimonialDTO).collect(Collectors.toList()))
                .myApplications(applicationCaseRepository.findByUserId(user.getId())
                        .stream().map(this::toApplicationCaseDTO).collect(Collectors.toList()))
                .build();
    }

    private LeadDTO toLeadDTO(Lead lead) {
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

    private TestimonialDTO toTestimonialDTO(Testimonial testimonial) {
        return TestimonialDTO.builder()
                .id(testimonial.getId())
                .clientName(testimonial.getClientName())
                .clientRole(testimonial.getClientRole())
                .serviceUsed(testimonial.getServiceUsed())
                .quote(testimonial.getQuote())
                .rating(testimonial.getRating())
                .location(testimonial.getLocation())
                .approved(testimonial.isApproved())
                .build();
    }

    private ApplicationCaseDTO toApplicationCaseDTO(ApplicationCase applicationCase) {
        return ApplicationCaseDTO.builder()
                .id(applicationCase.getId())
                .referenceNumber(applicationCase.getReferenceNumber())
                .applicantName(applicationCase.getApplicantName())
                .serviceType(applicationCase.getServiceType())
                .currentStage(applicationCase.getCurrentStage())
                .createdAt(applicationCase.getCreatedAt())
                .updatedAt(applicationCase.getUpdatedAt())
                .statusUpdates(applicationCase.getStatusUpdates().stream()
                        .sorted(Comparator.comparing(CaseStatusUpdate::getUpdatedAt))
                        .map(this::toStatusUpdateDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    private CaseStatusUpdateDTO toStatusUpdateDTO(CaseStatusUpdate statusUpdate) {
        return CaseStatusUpdateDTO.builder()
                .stage(statusUpdate.getStage())
                .note(statusUpdate.getNote())
                .updatedAt(statusUpdate.getUpdatedAt())
                .build();
    }
}