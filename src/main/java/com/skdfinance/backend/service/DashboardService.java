package com.skdfinance.backend.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.skdfinance.backend.dto.DashboardStatsDTO;
import com.skdfinance.backend.repository.ApplicationCaseRepository;
import com.skdfinance.backend.repository.BlogPostRepository;
import com.skdfinance.backend.repository.LeadRepository;
import com.skdfinance.backend.repository.TestimonialRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final LeadRepository leadRepository;
    private final ApplicationCaseRepository applicationCaseRepository;
    private final BlogPostRepository blogPostRepository;
    private final TestimonialRepository testimonialRepository;

    public DashboardStatsDTO getStats() {
        return DashboardStatsDTO.builder()
                .totalLeads(leadRepository.count())
                .newLeads(leadRepository.countByStatus("NEW"))
                .totalCases(applicationCaseRepository.count())
                .publishedBlogPosts(blogPostRepository.countByPublishedAtLessThanEqual(LocalDate.now()))
                .pendingTestimonials(testimonialRepository.countByApprovedFalse())
                .approvedTestimonials(testimonialRepository.countByApprovedTrue())
                .build();
    }
}