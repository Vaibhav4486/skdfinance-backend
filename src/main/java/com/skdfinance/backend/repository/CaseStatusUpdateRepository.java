package com.skdfinance.backend.repository;

import com.skdfinance.backend.entity.CaseStatusUpdate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaseStatusUpdateRepository extends JpaRepository<CaseStatusUpdate, Long> {

    List<CaseStatusUpdate> findByApplicationCaseIdOrderByUpdatedAtAsc(Long applicationCaseId);
}
