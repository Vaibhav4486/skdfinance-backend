package com.skdfinance.backend.controller;

import com.skdfinance.backend.dto.BlogPostDTO;
import com.skdfinance.backend.dto.BlogPostSummaryDTO;
import com.skdfinance.backend.service.BlogPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BlogPostController {

    private final BlogPostService blogPostService;

    @GetMapping("/api/blog")
    public ResponseEntity<List<BlogPostSummaryDTO>> getPublishedPosts() {
        return ResponseEntity.ok(blogPostService.getPublishedPosts());
    }

    @GetMapping("/api/blog/{slug}")
    public ResponseEntity<BlogPostDTO> getPostBySlug(@PathVariable String slug) {
        return ResponseEntity.ok(blogPostService.getPostBySlug(slug));
    }

    @PostMapping("/api/admin/blog")
    public ResponseEntity<BlogPostDTO> createPost(@Valid @RequestBody BlogPostDTO request) {
        BlogPostDTO created = blogPostService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/api/admin/blog/{id}")
    public ResponseEntity<BlogPostDTO> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody BlogPostDTO request) {
        return ResponseEntity.ok(blogPostService.updatePost(id, request));
    }

    @DeleteMapping("/api/admin/blog/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        blogPostService.deletePost(id);
        return ResponseEntity.noContent().build();
    }
}
