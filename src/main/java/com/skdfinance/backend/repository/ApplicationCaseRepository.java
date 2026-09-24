package com.skdfinance.backend.repository;

import com.skdfinance.backend.entity.ApplicationCase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationCaseRepository extends JpaRepository<ApplicationCase, Long> {

    // this is the lookup the public "track status" page will call
    Optional<ApplicationCase> findByReferenceNumber(String referenceNumber);

    boolean existsByReferenceNumber(String referenceNumber);

    List<ApplicationCase> findByUserId(Long userId);
}