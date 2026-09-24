package com.skdfinance.backend.repository;

import com.skdfinance.backend.entity.Faq;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaqRepository extends JpaRepository<Faq, Long> {

    List<Faq> findByPublishedTrueOrderByDisplayOrderAsc();

    List<Faq> findAllByOrderByDisplayOrderAsc();

    List<Faq> findByCategoryAndPublishedTrueOrderByDisplayOrderAsc(String category);
}