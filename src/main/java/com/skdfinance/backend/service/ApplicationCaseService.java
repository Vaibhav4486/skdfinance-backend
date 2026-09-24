package com.skdfinance.backend.service;

import com.skdfinance.backend.dto.ApplicationCaseCreateDTO;
import com.skdfinance.backend.dto.ApplicationCaseDTO;
import com.skdfinance.backend.dto.CaseStatusUpdateDTO;
import com.skdfinance.backend.dto.CaseStatusUpdateRequestDTO;
import com.skdfinance.backend.entity.ApplicationCase;
import com.skdfinance.backend.entity.CaseStatusUpdate;
import com.skdfinance.backend.entity.User;
import com.skdfinance.backend.repository.ApplicationCaseRepository;
import com.skdfinance.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationCaseService {

    private static final String INITIAL_STAGE = "Documents Submitted";

    private final ApplicationCaseRepository applicationCaseRepository;
    private final UserRepository userRepository;

    @Transactional
    public ApplicationCaseDTO createCase(ApplicationCaseCreateDTO request) {
        // applicantName/phone/serviceType shape is already covered by
        // @NotBlank/@Pattern on ApplicationCaseCreateDTO via @Valid

        String referenceNumber = generateUniqueReferenceNumber();
        LocalDateTime now = LocalDateTime.now();

        // best-effort: if the walk-in customer already has an account under
        // this phone number, link the case so it shows in their dashboard.
        // No match just means it stays null — the case still works fine
        // looked up by reference number either way.
        User matchedUser = userRepository.findByPhone(request.getPhone()).orElse(null);

        ApplicationCase applicationCase = ApplicationCase.builder()
                .referenceNumber(referenceNumber)
                .applicantName(request.getApplicantName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .serviceType(request.getServiceType())
                .assignedAdvisor(request.getAssignedAdvisor())
                .currentStage(INITIAL_STAGE)
                .createdAt(now)
                .updatedAt(now)
                .user(matchedUser)
                .build();

        ApplicationCase saved = applicationCaseRepository.save(applicationCase);
        return toDTO(saved);
    }

    public ApplicationCaseDTO getCaseByReferenceNumber(String referenceNumber) {
        ApplicationCase applicationCase = applicationCaseRepository.findByReferenceNumber(referenceNumber)
                // same generic message regardless of why it failed — never let someone
                // fish for which reference numbers are "close" to a real one
                .orElseThrow(() -> new NoSuchElementException("No application found for this reference number"));

        return toDTO(applicationCase);
    }

    @Transactional
    public ApplicationCaseDTO addStatusUpdate(Long caseId, CaseStatusUpdateRequestDTO request) {
        // stage shape is already covered by @NotBlank on CaseStatusUpdateRequestDTO via @Valid

        ApplicationCase applicationCase = applicationCaseRepository.findById(caseId)
                .orElseThrow(() -> new NoSuchElementException("Case not found: " + caseId));

        LocalDateTime now = LocalDateTime.now();

        CaseStatusUpdate statusUpdate = CaseStatusUpdate.builder()
                .applicationCase(applicationCase)
                .stage(request.getStage())
                .note(request.getNote())
                .updatedAt(now)
                .build();

        // both writes happen in the same @Transactional method so the case's
        // currentStage can never drift out of sync with its own timeline
        applicationCase.getStatusUpdates().add(statusUpdate);
        applicationCase.setCurrentStage(request.getStage());
        applicationCase.setUpdatedAt(now);

        ApplicationCase saved = applicationCaseRepository.save(applicationCase);
        return toDTO(saved);
    }

    private String generateUniqueReferenceNumber() {
        String referenceNumber;
        do {
            int sequence = ThreadLocalRandom.current().nextInt(1, 100_000);
            referenceNumber = "SKD-" + Year.now().getValue() + "-" + String.format("%05d", sequence);
        } while (applicationCaseRepository.existsByReferenceNumber(referenceNumber));
        // random + existence check is fine at this scale; if case volume grows large,
        // swap this for a DB sequence so it can't retry under load

        return referenceNumber;
    }

    private ApplicationCaseDTO toDTO(ApplicationCase applicationCase) {
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