package com.cognizant.taxease.reportingService.service.impl;

import com.cognizant.taxease.reportingService.dao.*;
import com.cognizant.taxease.reportingService.entity.Audit;
import com.cognizant.taxease.reportingService.entity.ComplianceRecord;
import com.cognizant.taxease.reportingService.entity.RevenueRecord;
import com.cognizant.taxease.reportingService.entity.entityEnum.PaymentMethod;
import com.cognizant.taxease.reportingService.entity.entityEnum.StatusBasic;
import com.cognizant.taxease.reportingService.service.ReportService;
import com.cognizant.taxease.reportingService.dto.responsedto.PaymentMetricsResponse;
import com.cognizant.taxease.reportingService.dto.responsedto.AuditDashboardResponse;
import com.cognizant.taxease.reportingService.dto.responsedto.RevenueDashboardResponse;
import com.cognizant.taxease.reportingService.dto.responsedto.AuditResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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

    // 🚀 Updated Method: Pagination & Entity to DTO Mapping
    @Override
    public Page<AuditResponse> getCompletedAudits(Pageable pageable) {
        // Step 1: Database-la irunthu Page-a edukkurom
        Page<Audit> auditPage = auditRepository.findByStatus(StatusBasic.Completed, pageable);

        // Step 2: Un original entity-ku yetha mathiri correct-a map pandrom
        return auditPage.map(audit -> AuditResponse.builder()
                .id(audit.getId())
                .officerId(audit.getOfficer() != null ? audit.getOfficer().getId() : null)
                .scope(audit.getScope())
                .findings(audit.getFindings())
                .createdAt(audit.getCreatedAt())
                .status(audit.getStatus())
                .build()
        );
    }

    @Override
    public byte[] generateCustomReport(LocalDate startDate, LocalDate endDate, String reportType, List<String> metrics) {
        StringBuilder csv = new StringBuilder();

        csv.append("TaxEase Dynamic Custom Report\n");
        csv.append("Report Type:,").append(reportType).append("\n");
        csv.append("Date Range:,").append(startDate).append(",to,").append(endDate).append("\n\n");

        Instant startInstant = startDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant endInstant = endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant();

        if (metrics.contains("Revenue")) {
            csv.append("--- REVENUE DATA ---\n");
            csv.append("Revenue ID,Taxpayer ID,Amount,Date,Status\n");

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

        if (metrics.contains("Compliance")) {
            csv.append("--- COMPLIANCE DATA ---\n");
            csv.append("Compliance ID,Taxpayer ID,Type,Result,Date,Notes\n");

            List<ComplianceRecord> compliances = complianceRepository.findByDateBetween(startDate, endDate);

            if (compliances.isEmpty()) {
                csv.append("No compliance records found for this period.\n");
            } else {
                for (ComplianceRecord c : compliances) {
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