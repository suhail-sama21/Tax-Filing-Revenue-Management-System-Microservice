package com.cognizant.auditservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditDashboardResponse {

    private long totalCases;
    private long open;
    private long inProgress;
    private long closed;
}