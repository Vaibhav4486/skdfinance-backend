package com.skdfinance.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "testimonials")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Testimonial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String clientName;

    private String clientRole;

    @Column(nullable = false)
    private String serviceUsed;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String quote;

    private Integer rating;

    private String location;

    // an advisor must approve a testimonial before it appears publicly
    @Column(nullable = false)
    private boolean approved;

    // nullable — null means a guest submission with no account attached
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}