package com.skdfinance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The at-a-glance numbers an advisor wants on opening the admin panel.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardStatsDTO {

    private long totalLeads;
    private long newLeads;
    private long totalCases;
    private long publishedBlogPosts;
    private long pendingTestimonials;
    private long approvedTestimonials;
}