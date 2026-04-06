package com.cognizant.taxease.reportingService.service;

import com.cognizant.taxease.reportingService.dto.*;
import java.time.LocalDate;
import java.util.List;

public interface ReportService {
    PaymentMetricsResponse getPaymentMetrics();
    AuditDashboardResponse getAuditDashboard();
    RevenueDashboardResponse getRevenueDashboard(String period, String taxpayerType);
    List<AuditDto> getCompletedAudits();
    byte[] generateCustomReport(LocalDate startDate, LocalDate endDate, String reportType, List<String> metrics);
}