package com.skdfinance.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * For the /resources listing grid — no need to ship the full
 * article body just to render cards.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPostSummaryDTO {

    private String title;
    private String slug;
    private String category;
    private String excerpt;
    private String author;
    private Integer readTimeMinutes;
    private LocalDate publishedAt;
    private String imageUrl;
}
