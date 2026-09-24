package com.skdfinance.backend.repository;

import com.skdfinance.backend.entity.ServiceOffering;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ServiceOfferingRepository extends JpaRepository<ServiceOffering, Long> {

    Optional<ServiceOffering> findBySlug(String slug);

    List<ServiceOffering> findAllByOrderByDisplayOrderAsc();
}
