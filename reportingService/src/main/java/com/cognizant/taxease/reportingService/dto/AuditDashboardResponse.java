package com.cognizant.taxease.reportingService.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditDashboardResponse {
    private long totalAudits;
    private long openAudits;
    private long closedAudits;
    private long nonComplianceFilings;
}