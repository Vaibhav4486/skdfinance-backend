package com.skdfinance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A logged-in customer's own history — everything they've submitted, tied to
 * their account. Entirely separate from DashboardStatsDTO (the admin view) —
 * different endpoint, different auth requirement, different shape.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDashboardDTO {

    private String fullName;
    private String email;
    private List<LeadDTO> myEnquiries;
    private List<TestimonialDTO> myTestimonials;
    private List<ApplicationCaseDTO> myApplications;
}