package com.skdfinance.backend.service;

import com.skdfinance.backend.dto.TestimonialDTO;
import com.skdfinance.backend.entity.Testimonial;
import com.skdfinance.backend.entity.User;
import com.skdfinance.backend.repository.TestimonialRepository;
import com.skdfinance.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TestimonialService {

    private final TestimonialRepository testimonialRepository;
    private final UserRepository userRepository;

    public List<TestimonialDTO> getApprovedTestimonials(String serviceUsed) {
        List<Testimonial> testimonials = StringUtils.hasText(serviceUsed)
                ? testimonialRepository.findByServiceUsedAndApprovedTrue(serviceUsed)
                : testimonialRepository.findByApprovedTrue();

        return testimonials.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public TestimonialDTO submitTestimonial(TestimonialDTO request) {
        Testimonial testimonial = Testimonial.builder()
                .clientName(request.getClientName())
                .clientRole(request.getClientRole())
                .serviceUsed(request.getServiceUsed())
                .quote(request.getQuote())
                .rating(request.getRating())
                .location(request.getLocation())
                .approved(false) // never trust a client-supplied approval flag — the one rule
                .user(getCurrentUser().orElse(null)) // the DTO's @NotBlank annotations can't enforce on their own
                .build();

        Testimonial saved = testimonialRepository.save(testimonial);
        return toDTO(saved);
    }

    private Optional<User> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }
        return userRepository.findByEmail(authentication.getName());
    }

    public TestimonialDTO approveTestimonial(Long id) {
        Testimonial testimonial = testimonialRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Testimonial not found: " + id));

        testimonial.setApproved(true);
        Testimonial saved = testimonialRepository.save(testimonial);
        return toDTO(saved);
    }

    private TestimonialDTO toDTO(Testimonial testimonial) {
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
}