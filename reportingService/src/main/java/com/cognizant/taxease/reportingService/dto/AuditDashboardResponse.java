package com.cognizant.taxease.reportingService.dto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuditDashboardResponse {
    private long totalAudits;
    private long openAudits;
    private long closedAudits;
    private long nonComplianceFilings;
}