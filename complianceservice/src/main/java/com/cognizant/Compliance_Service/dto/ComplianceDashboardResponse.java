package com.cognizant.Compliance_Service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceDashboardResponse {

    private long totalChecks;
    private long pendingReviews;
    private long nonCompliant;
    private long compliant;

    private double systemHealth;
}