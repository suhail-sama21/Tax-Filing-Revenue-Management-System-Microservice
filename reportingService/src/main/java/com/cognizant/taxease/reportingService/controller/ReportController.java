package com.cognizant.taxease.reportingService.controller;

import com.cognizant.taxease.reportingService.dto.responsedto.PaymentMetricsResponse;
import com.cognizant.taxease.reportingService.dto.responsedto.AuditDashboardResponse;
import com.cognizant.taxease.reportingService.dto.responsedto.RevenueDashboardResponse;
import com.cognizant.taxease.reportingService.dto.responsedto.AuditResponse;
import com.cognizant.taxease.reportingService.entity.entityEnum.PaymentMethod;
import com.cognizant.taxease.reportingService.service.ReportService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/payments/metrics")
    public ResponseEntity<PaymentMetricsResponse> getPaymentMetrics(
            @RequestParam(required = false) PaymentMethod method) {
        log.info("START: Fetching payment metrics for method: {}", method);
        PaymentMetricsResponse response = reportService.getPaymentMetrics(method);
        log.info("END: Payment metrics retrieved");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/audits/dashboard")
    public ResponseEntity<AuditDashboardResponse> getAuditDashboard() {
        log.info("START: Fetching audit dashboard");
        AuditDashboardResponse response = reportService.getAuditDashboard();
        log.info("END: Audit dashboard retrieved");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/audits/completed")
    public ResponseEntity<Page<AuditResponse>> getCompletedAudits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("START: Fetching completed audits | Page: {} | Size: {}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<AuditResponse> response = reportService.getCompletedAudits(pageable);

        log.info("END: Completed audits retrieved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/revenue/dashboard")
    public ResponseEntity<RevenueDashboardResponse> getRevenueDashboard(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String taxpayerType) {
        log.info("START: Fetching revenue dashboard for period: {} | Type: {}", period, taxpayerType);
        RevenueDashboardResponse response = reportService.getRevenueDashboard(period, taxpayerType);
        log.info("END: Revenue dashboard retrieved");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/custom/download")
    public ResponseEntity<byte[]> downloadCustomReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reportType,
            @Valid @NotEmpty(message = "At least one metric must be selected") @RequestParam List<String> metrics) {

        log.info("START: Generating Custom Report [{}] | Period: {} to {}", reportType, startDate, endDate);
        byte[] reportData = reportService.generateCustomReport(startDate, endDate, reportType, metrics);
        log.info("END: Report generated | Size: {} bytes", reportData.length);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(reportData);
    }
}