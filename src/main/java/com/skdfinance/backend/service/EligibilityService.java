package com.skdfinance.backend.service;

import com.skdfinance.backend.dto.EligibilityRequestDTO;
import com.skdfinance.backend.dto.EligibilityResultDTO;
import com.skdfinance.backend.entity.EligibilityAssessment;
import com.skdfinance.backend.repository.EligibilityAssessmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EligibilityService {

    // FOIR = Fixed Obligation to Income Ratio. 0.5 means lenders generally
    // won't let total EMI obligations exceed 50% of monthly income.
    // Tune this constant if you get real underwriting guidance later.
    private static final BigDecimal FOIR_LIMIT = BigDecimal.valueOf(0.5);

    private final EligibilityAssessmentRepository eligibilityAssessmentRepository;

    public EligibilityResultDTO checkEligibility(EligibilityRequestDTO request) {
        // income > 0 / age 18-75 / CIBIL 300-900 / required fields are already
        // covered by the DTO's annotations via @Valid in the controller.
        // What's left here are business rules the annotations can't express.

        BigDecimal existingEmi = request.getExistingEmi() != null ? request.getExistingEmi() : BigDecimal.ZERO;

        if (existingEmi.compareTo(request.getMonthlyIncome()) >= 0) {
            throw new IllegalArgumentException("Existing EMI cannot equal or exceed monthly income");
        }

        BigDecimal maxAffordableEmi = request.getMonthlyIncome()
                .multiply(FOIR_LIMIT)
                .subtract(existingEmi);

        BigDecimal eligibleAmount;
        Integer eligibilityScore;
        String recommendation;

        if (maxAffordableEmi.compareTo(BigDecimal.ZERO) <= 0) {
            eligibleAmount = BigDecimal.ZERO;
            eligibilityScore = 0;
            recommendation = "Existing obligations leave no room for a new EMI.";
        } else {
            double[] rateAndTenure = getRateAndTenure(request.getLoanType());
            eligibleAmount = calculatePrincipal(maxAffordableEmi, rateAndTenure[0], (int) rateAndTenure[1]);
            eligibilityScore = calculateScore(request);
            recommendation = buildRecommendation(eligibilityScore);
        }

        List<String> qualifiesFor = buildQualifiesForList(request.getLoanType(), eligibleAmount);

        // save regardless of outcome — even a poor result is a lead an advisor should follow up with
        EligibilityAssessment assessment = EligibilityAssessment.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .monthlyIncome(request.getMonthlyIncome())
                .age(request.getAge())
                .employmentType(request.getEmploymentType())
                .existingEmi(existingEmi)
                .cibilScore(request.getCibilScore())
                .city(request.getCity())
                .loanType(request.getLoanType())
                .eligibleAmount(eligibleAmount)
                .eligibilityScore(eligibilityScore)
                .recommendation(recommendation)
                .createdAt(LocalDateTime.now())
                .build();

        eligibilityAssessmentRepository.save(assessment);

        return EligibilityResultDTO.builder()
                .eligibilityScore(eligibilityScore)
                .eligibleAmount(eligibleAmount)
                .recommendation(recommendation)
                .qualifiesFor(qualifiesFor)
                .build();
    }

    /**
     * Assumed annual rate (%) and tenure (years) per loan type — midpoints
     * of the ranges published on the service pages. Returns {rate, tenureYears}.
     */
    private double[] getRateAndTenure(String loanType) {
        return switch (loanType) {
            case "Home Loan" -> new double[]{9.0, 20};
            case "Mortgage Loan" -> new double[]{10.5, 12};
            case "Open Plot Purchase" -> new double[]{9.5, 12};
            case "Business Loan" -> new double[]{13.0, 5};
            case "4 Wheeler Loan" -> new double[]{10.5, 6};
            default -> new double[]{11.0, 10};
        };
    }

    /**
     * Reverse-calculates the loan principal a given EMI can support, using the
     * standard amortization formula: P = EMI * [(1+r)^n - 1] / [r * (1+r)^n]
     * where r = monthly interest rate, n = tenure in months.
     */
    private BigDecimal calculatePrincipal(BigDecimal affordableEmi, double annualRatePercent, int tenureYears) {
        double monthlyRate = (annualRatePercent / 12) / 100;
        int totalMonths = tenureYears * 12;
        double emiValue = affordableEmi.doubleValue();

        double growthFactor = Math.pow(1 + monthlyRate, totalMonths);
        double principal = emiValue * (growthFactor - 1) / (monthlyRate * growthFactor);

        return BigDecimal.valueOf(principal).setScale(0, RoundingMode.HALF_UP);
    }

    /**
     * 0-100 score: CIBIL contributes up to 60, income adequacy up to 25,
     * employment stability up to 15. These weights are a starting point —
     * revisit them once you have real approval-outcome data to calibrate against.
     */
    private Integer calculateScore(EligibilityRequestDTO request) {
        int score;

        if (request.getCibilScore() != null) {
            int cibil = request.getCibilScore();
            score = (int) Math.round(((cibil - 300) / 600.0) * 60);
        } else {
            score = 30; // no score on file — treat as a neutral midpoint, not a penalty
        }

        double incomeRatio = request.getMonthlyIncome().doubleValue() / 25000.0;
        score += (int) Math.min(25, Math.round(incomeRatio * 5));

        score += "Salaried".equalsIgnoreCase(request.getEmploymentType()) ? 15 : 10;

        return Math.min(100, score);
    }

    private String buildRecommendation(int score) {
        if (score >= 75) {
            return "Excellent eligibility.";
        } else if (score >= 50) {
            return "Good eligibility, some conditions may apply.";
        } else {
            return "Limited eligibility — consider a longer tenure or a co-applicant.";
        }
    }

    private List<String> buildQualifiesForList(String requestedLoanType, BigDecimal eligibleAmount) {
        List<String> qualifiesFor = new ArrayList<>();
        qualifiesFor.add(requestedLoanType);

        if (eligibleAmount.compareTo(BigDecimal.valueOf(500_000)) >= 0
                && !qualifiesFor.contains("4 Wheeler Loan")) {
            qualifiesFor.add("4 Wheeler Loan");
        }
        if (eligibleAmount.compareTo(BigDecimal.valueOf(2_000_000)) >= 0
                && !qualifiesFor.contains("Business Loan")) {
            qualifiesFor.add("Business Loan");
        }

        return qualifiesFor;
    }
}
