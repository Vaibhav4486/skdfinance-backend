package com.skdfinance.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Full article — used for the single-post page, and for the admin
 * create/edit form.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlogPostDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Slug is required")
    private String slug;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Excerpt is required")
    private String excerpt;

    @NotBlank(message = "Content is required")
    private String content;

    private String author;
    private Integer readTimeMinutes;
    private LocalDate publishedAt;
    private String imageUrl;
}
