package com.skdfinance.backend.repository;

import com.skdfinance.backend.entity.Testimonial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestimonialRepository extends JpaRepository<Testimonial, Long> {

    // only these should ever be shown on the public site
    List<Testimonial> findByApprovedTrue();

    List<Testimonial> findByServiceUsedAndApprovedTrue(String serviceUsed);

    long countByApprovedTrue();

    long countByApprovedFalse();

    // a customer should see their own submissions regardless of approval status
    List<Testimonial> findByUserId(Long userId);
}