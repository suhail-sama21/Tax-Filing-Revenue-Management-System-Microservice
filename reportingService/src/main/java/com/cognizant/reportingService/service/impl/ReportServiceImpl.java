package com.cognizant.reportingService.service.impl;

import com.cognizant.paymentService.dto.responsedto.PaymentResponseDto;
import com.cognizant.reportingService.client.AuditClient;
import com.cognizant.reportingService.client.ComplianceClient;
import com.cognizant.reportingService.client.PaymentClient;
import com.cognizant.reportingService.dto.*;
import com.cognizant.reportingService.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentClient paymentClient;
    private final AuditClient auditClient;
    private final ComplianceClient complianceClient;

    @Override
    public PaymentMetricsResponse getPaymentMetrics(String method) {
        try {
            return paymentClient.getPaymentMetrics(method);
        } catch (Exception e) {
            throw new RuntimeException("Validation Error: " + e.getMessage());
        }
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
        return paymentClient.getRevenueDashboard(period, taxpayerType);
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

        // --- COMPLIANCE DATA ---
        if (metrics.contains("Compliance")) {
            csv.append("--- COMPLIANCE DATA ---\n");
            csv.append("Compliance ID,Taxpayer ID,Type,Result,Date,Notes\n");

            try {
                List<ComplianceDto> allCompliance = complianceClient.getAllCompliance();

                if (allCompliance == null || allCompliance.isEmpty()) {
                    csv.append("No compliance records found in the database.\n");
                } else {
                    // SAFE FILTER: Added c.getDate() != null to prevent NullPointerException!
                    List<ComplianceDto> compliance = allCompliance.stream()
                            .filter(c -> c.getDate() != null && !c.getDate().isBefore(startDate) && !c.getDate().isAfter(endDate))
                            .toList();

                    if (compliance.isEmpty()) {
                        csv.append("No compliance records found for this date range.\n");
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
                }
            } catch (Exception e) {
                // Safeguard if ComplianceService is down or throws 403
                csv.append("Error: Could not retrieve compliance data. (").append(e.getMessage()).append(")\n");
            }
            csv.append("\n");
        }

        // --- REVENUE DATA ---
        if (metrics.contains("Revenue")) {
            csv.append("--- REVENUE DATA ---\n");
            csv.append("Transaction ID,Taxpayer ID,Amount,Status,Date,Method\n");

            try {
                List<PaymentResponseDto> payments = paymentClient.getAllPayments();

                if (payments == null || payments.isEmpty()) {
                    csv.append("No revenue records found in the database.\n");
                } else {
                    boolean foundRevenue = false;
                    for (PaymentResponseDto p : payments) {
                        if (p.getDate() != null) { // Null check
                            LocalDate pDate = LocalDate.ofInstant(p.getDate(), ZoneId.systemDefault());

                            if (!pDate.isBefore(startDate) && !pDate.isAfter(endDate)) {
                                foundRevenue = true;
                                csv.append(p.getId()).append(",")
                                        .append(p.getTaxpayerId()).append(",")
                                        .append(p.getAmount()).append(",")
                                        .append(p.getStatus()).append(",")
                                        .append(pDate).append(",")
                                        .append(p.getMethod()).append("\n");
                            }
                        }
                    }
                    if (!foundRevenue) {
                        csv.append("No revenue records found for this date range.\n");
                    }
                }
            } catch (Exception e) {
                csv.append("Error: Could not retrieve revenue data from payment-service. (").append(e.getMessage()).append(")\n");
            }
        }

        return csv.toString().getBytes();
    }

    @Override
    public List<PaymentResponseDto> getAllPayments() {
        return paymentClient.getAllPayments();
    }
}