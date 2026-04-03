package com.cognizant.taxease.reportingService.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditDashboardResponse {
    private long totalAudits;
    private long openAudits;
    private long closedAudits;
    private long nonComplianceFilings;
}