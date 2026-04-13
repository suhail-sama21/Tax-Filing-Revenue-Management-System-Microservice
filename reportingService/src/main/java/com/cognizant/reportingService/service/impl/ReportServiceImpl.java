package com.cognizant.reportingService.service.impl;

import com.cognizant.reportingService.client.AuditClient;
import com.cognizant.reportingService.client.ComplianceClient;
import com.cognizant.reportingService.client.PaymentClient;
import com.cognizant.reportingService.dto.*;
import com.cognizant.reportingService.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentClient paymentClient;
    private final AuditClient auditClient;
    private final ComplianceClient complianceClient;

    @Override
    public PaymentMetricsResponse getPaymentMetrics() {
        return paymentClient.getPaymentMetrics();
    }

    @Override
    public AuditDashboardResponse getAuditDashboard() {
        List<AuditDto> allAudits = auditClient.getAllAudits();
        List<ComplianceDto> nonCompliant = complianceClient.getByResult("Non-Compliant");

        long open = allAudits.stream().filter(a -> "Active".equalsIgnoreCase(a.getStatus())).count();
        long closed = allAudits.stream().filter(a -> "Inactive".equalsIgnoreCase(a.getStatus())).count();

        return AuditDashboardResponse.builder()
                .totalAudits(allAudits.size())
                .openAudits(open)
                .closedAudits(closed)
                .nonComplianceFilings(nonCompliant.size())
                .build();
    }

    @Override
    public RevenueDashboardResponse getRevenueDashboard(String period, String taxpayerType) {
        return paymentClient.getRevenueDashboard();
    }

    @Override
    public List<AuditDto> getCompletedAudits() {
        return auditClient.getAllAudits().stream()
                .filter(a -> "Inactive".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());
    }

    @Override
    public byte[] generateCustomReport(LocalDate startDate, LocalDate endDate, String reportType, List<String> metrics) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date!");
        }

        StringBuilder csv = new StringBuilder();

        csv.append("TaxEase Dynamic Custom Report\n");
        csv.append("Report Type:,").append(reportType).append("\n");
        csv.append("Date Range:,").append(startDate).append(",to,").append(endDate).append("\n\n");

        if (metrics.contains("Compliance")) {
            csv.append("--- COMPLIANCE DATA ---\n");
            csv.append("Compliance ID,Taxpayer ID,Type,Result,Date,Notes\n");

            List<ComplianceDto> compliance = complianceClient.getAllCompliance().stream()
                    .filter(c -> !c.getDate().isBefore(startDate) && !c.getDate().isAfter(endDate))
                    .toList();

            if (compliance.isEmpty()) {
                csv.append("No compliance records found for this period.\n");
            } else {
                for (ComplianceDto c : compliance) {
                    String safeNotes = c.getNotes() != null ? c.getNotes().replace(",", " ") : "N/A";
                    csv.append(c.getId()).append(",")
                            .append(c.getTaxpayerId()).append(",")
                            .append(c.getType()).append(",")
                            .append(c.getResult()).append(",")
                            .append(c.getDate()).append(",")
                            .append(safeNotes).append("\n");
                }
            }
            csv.append("\n");
        }

        return csv.toString().getBytes();
    }
}