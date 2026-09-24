package com.skdfinance.backend.repository;

import com.skdfinance.backend.entity.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LeadRepository extends JpaRepository<Lead, Long> {

    List<Lead> findByStatus(String status);

    List<Lead> findByServiceInterested(String serviceInterested);

    long countByStatus(String status);

    List<Lead> findByUserId(Long userId);
}