package com.cognizant.paymentService.service.impl;

import com.cognizant.taxease.dao.*;
import com.cognizant.taxease.entity.Audit;
import com.cognizant.taxease.entity.ComplianceRecord;
import com.cognizant.taxease.entity.RevenueRecord;
import com.cognizant.taxease.entity.entityEnum.PaymentMethod;
import com.cognizant.taxease.entity.entityEnum.StatusBasic;
import com.cognizant.taxease.service.ReportService;
import com.cognizant.taxease.dto.responsedto.PaymentMetricsResponse;
import com.cognizant.taxease.dto.responsedto.AuditDashboardResponse;
import com.cognizant.taxease.dto.responsedto.RevenueDashboardResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final PaymentRepository paymentRepository;
    private final AuditRepository auditRepository;
    private final RevenueRecordRepository revenueRepository;
    private final ComplianceRecordRepository complianceRepository;

    @Override
    public PaymentMetricsResponse getPaymentMetrics(PaymentMethod method) {
        long success = method != null ? paymentRepository.countByStatusAndMethod(StatusBasic.Completed, method)
                : paymentRepository.countByStatus(StatusBasic.Completed);
        long failed = method != null  ? paymentRepository.countByStatusAndMethod(StatusBasic.Failed, method)
                : paymentRepository.countByStatus(StatusBasic.Failed);
        long total = method != null   ? paymentRepository.countByMethod(method)
                : paymentRepository.count();

        return PaymentMetricsResponse.builder()
                .successfulTransactions(success)
                .failedTransactions(failed)
                .totalTransactions(total)
                .build();
    }

    @Override
    public AuditDashboardResponse getAuditDashboard() {
        return AuditDashboardResponse.builder()
                .totalAudits(auditRepository.count())
                .openAudits(auditRepository.countByStatus(StatusBasic.Pending))
                .closedAudits(auditRepository.countByStatus(StatusBasic.Completed))
                .nonComplianceFilings(complianceRepository.countByResultIgnoreCase("Non-Compliant"))
                .build();
    }

    @Override
    public RevenueDashboardResponse getRevenueDashboard(String period, String taxpayerType) {
        BigDecimal collected = revenueRepository.sumCollectedRevenue();
        BigDecimal outstanding = paymentRepository.sumOutstandingPayments();

        return RevenueDashboardResponse.builder()
                .revenueCollected(collected != null ? collected : BigDecimal.ZERO)
                .outstandingPayments(outstanding != null ? outstanding : BigDecimal.ZERO)
                .build();
    }

    @Override
    public List<Audit> getCompletedAudits() {
        return auditRepository.findByStatus(StatusBasic.Completed);
    }

    @Override
    public byte[] generateCustomReport(LocalDate startDate, LocalDate endDate, String reportType, List<String> metrics) {
        StringBuilder csv = new StringBuilder();

        // 1. Build the Report Header
        csv.append("TaxEase Dynamic Custom Report\n");
        csv.append("Report Type:,").append(reportType).append("\n");
        csv.append("Date Range:,").append(startDate).append(",to,").append(endDate).append("\n\n");

        // Convert LocalDate to Instant for tables that use Instant for timestamps (like RevenueRecord)
        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

        // 2. Fetch and append Revenue Data if requested
        if (metrics.contains("Revenue")) {
            csv.append("--- REVENUE DATA ---\n");
            csv.append("Revenue ID,Taxpayer ID,Amount,Date,Status\n"); // Column Headers

            List<RevenueRecord> revenues = revenueRepository.findByDateBetween(startInstant, endInstant);

            if (revenues.isEmpty()) {
                csv.append("No revenue records found for this period.\n");
            } else {
                for (RevenueRecord r : revenues) {
                    csv.append(r.getId()).append(",")
                            .append(r.getTaxpayer().getId()).append(",")
                            .append(r.getAmount()).append(",")
                            .append(r.getDate()).append(",")
                            .append(r.getStatus()).append("\n");
                }
            }
            csv.append("\n");
        }

        // 3. Fetch and append Compliance Data if requested
        if (metrics.contains("Compliance")) {
            csv.append("--- COMPLIANCE DATA ---\n");
            csv.append("Compliance ID,Taxpayer ID,Type,Result,Date,Notes\n"); // Column Headers

            List<ComplianceRecord> compliances = complianceRepository.findByDateBetween(startDate, endDate);

            if (compliances.isEmpty()) {
                csv.append("No compliance records found for this period.\n");
            } else {
                for (ComplianceRecord c : compliances) {
                    // We replace commas in the notes with spaces so they don't break the CSV columns!
                    String safeNotes = c.getNotes() != null ? c.getNotes().replace(",", " ") : "N/A";

                    csv.append(c.getId()).append(",")
                            .append(c.getTaxpayer().getId()).append(",")
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