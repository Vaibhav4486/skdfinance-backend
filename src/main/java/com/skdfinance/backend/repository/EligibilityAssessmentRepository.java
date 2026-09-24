package com.skdfinance.backend.repository;

import com.skdfinance.backend.entity.EligibilityAssessment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EligibilityAssessmentRepository extends JpaRepository<EligibilityAssessment, Long> {

    List<EligibilityAssessment> findByPhone(String phone);

    List<EligibilityAssessment> findByLoanType(String loanType);
}
