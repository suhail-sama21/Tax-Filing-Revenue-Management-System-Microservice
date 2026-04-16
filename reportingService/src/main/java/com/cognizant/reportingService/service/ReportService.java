package com.cognizant.reportingService.service;

import com.cognizant.reportingService.dto.AuditDashboardResponse;
import com.cognizant.reportingService.dto.AuditDto;
import com.cognizant.reportingService.dto.PaymentMetricsResponse;
import com.cognizant.reportingService.dto.RevenueDashboardResponse;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    PaymentMetricsResponse getPaymentMetrics(String method);

    AuditDashboardResponse getAuditDashboard();

    RevenueDashboardResponse getRevenueDashboard(String period, String taxpayerType);

    List<AuditDto> getCompletedAudits();

    byte[] generateCustomReport(LocalDate startDate, LocalDate endDate, String reportType, List<String> metrics);
}