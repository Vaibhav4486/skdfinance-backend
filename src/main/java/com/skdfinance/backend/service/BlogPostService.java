package com.skdfinance.backend.service;

import com.skdfinance.backend.dto.BlogPostDTO;
import com.skdfinance.backend.dto.BlogPostSummaryDTO;
import com.skdfinance.backend.entity.BlogPost;
import com.skdfinance.backend.repository.BlogPostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogPostService {

    private final BlogPostRepository blogPostRepository;

    public List<BlogPostSummaryDTO> getPublishedPosts() {
        LocalDate today = LocalDate.now();

        return blogPostRepository.findAllByOrderByPublishedAtDesc()
                .stream()
                // a future-dated post is "scheduled," not published — filter it out here
                // rather than deleting it, so it appears automatically once its date arrives
                .filter(post -> !post.getPublishedAt().isAfter(today))
                .map(this::toSummaryDTO)
                .collect(Collectors.toList());
    }

    public BlogPostDTO getPostBySlug(String slug) {
        BlogPost post = blogPostRepository.findBySlug(slug)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + slug));

        if (post.getPublishedAt().isAfter(LocalDate.now())) {
            // same message as the not-found case above — don't reveal that a
            // scheduled post exists just because someone guessed its slug
            throw new NoSuchElementException("Post not found: " + slug);
        }

        return toDTO(post);
    }

    public BlogPostDTO createPost(BlogPostDTO request) {
        blogPostRepository.findBySlug(request.getSlug())
                .ifPresent(existing -> {
                    throw new IllegalStateException("A post with this slug already exists");
                });

        BlogPost post = BlogPost.builder()
                .title(request.getTitle())
                .slug(request.getSlug())
                .category(request.getCategory())
                .excerpt(request.getExcerpt())
                .content(request.getContent())
                .author(request.getAuthor())
                .readTimeMinutes(request.getReadTimeMinutes())
                .publishedAt(request.getPublishedAt() != null ? request.getPublishedAt() : LocalDate.now())
                .imageUrl(request.getImageUrl())
                .build();

        BlogPost saved = blogPostRepository.save(post);
        return toDTO(saved);
    }

    public BlogPostDTO updatePost(Long id, BlogPostDTO request) {
        BlogPost existing = blogPostRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Post not found: " + id));

        if (!existing.getSlug().equals(request.getSlug())) {
            blogPostRepository.findBySlug(request.getSlug())
                    .ifPresent(other -> {
                        if (!other.getId().equals(id)) {
                            throw new IllegalStateException("A post with this slug already exists");
                        }
                    });
        }

        existing.setTitle(request.getTitle());
        existing.setSlug(request.getSlug());
        existing.setCategory(request.getCategory());
        existing.setExcerpt(request.getExcerpt());
        existing.setContent(request.getContent());
        existing.setAuthor(request.getAuthor());
        existing.setReadTimeMinutes(request.getReadTimeMinutes());
        if (request.getPublishedAt() != null) {
            existing.setPublishedAt(request.getPublishedAt());
        }
        existing.setImageUrl(request.getImageUrl());

        BlogPost saved = blogPostRepository.save(existing);
        return toDTO(saved);
    }

    public void deletePost(Long id) {
        if (!blogPostRepository.existsById(id)) {
            throw new NoSuchElementException("Post not found: " + id);
        }
        blogPostRepository.deleteById(id);
    }

    private BlogPostDTO toDTO(BlogPost post) {
        return BlogPostDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .slug(post.getSlug())
                .category(post.getCategory())
                .excerpt(post.getExcerpt())
                .content(post.getContent())
                .author(post.getAuthor())
                .readTimeMinutes(post.getReadTimeMinutes())
                .publishedAt(post.getPublishedAt())
                .imageUrl(post.getImageUrl())
                .build();
    }

    private BlogPostSummaryDTO toSummaryDTO(BlogPost post) {
        return BlogPostSummaryDTO.builder()
                .title(post.getTitle())
                .slug(post.getSlug())
                .category(post.getCategory())
                .excerpt(post.getExcerpt())
                .author(post.getAuthor())
                .readTimeMinutes(post.getReadTimeMinutes())
                .publishedAt(post.getPublishedAt())
                .imageUrl(post.getImageUrl())
                .build();
    }
}
