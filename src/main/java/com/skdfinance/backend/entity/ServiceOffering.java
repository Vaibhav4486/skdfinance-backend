package com.skdfinance.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Rates and limits change — keeping these as data (instead of hardcoded
 * frontend text) means an advisor can update "starting rate" without
 * a redeploy.
 */
@Entity
@Table(name = "service_offerings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceOffering {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, length = 500)
    private String shortDescription;

    private String startingRate;

    private String maxAmount;

    private String maxTenure;

    private String iconKey;

    @Column(nullable = false)
    private Integer displayOrder;
}
