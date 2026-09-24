package com.skdfinance.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skdfinance.backend.entity.BlogPost;

public interface BlogPostRepository extends JpaRepository<BlogPost, Long> {

    Optional<BlogPost> findBySlug(String slug);

    List<BlogPost> findByCategoryOrderByPublishedAtDesc(String category);

    List<BlogPost> findAllByOrderByPublishedAtDesc();

    long countByPublishedAtLessThanEqual(LocalDate date);
}