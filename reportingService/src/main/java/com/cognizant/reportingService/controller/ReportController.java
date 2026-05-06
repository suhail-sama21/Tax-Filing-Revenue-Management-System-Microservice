package com.cognizant.reportingService.controller;

import com.cognizant.reportingService.dto.AuditDashboardResponse;
import com.cognizant.reportingService.dto.AuditDto;
import com.cognizant.reportingService.dto.PaymentMetricsResponse;
import com.cognizant.reportingService.dto.RevenueDashboardResponse;
import com.cognizant.reportingService.service.ReportService;
import com.cognizant.paymentService.dto.responsedto.PaymentResponseDto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            @RequestParam(required = false) String method) {

        log.info("START: Fetching payment metrics for method: {}", method);
        return ResponseEntity.ok(reportService.getPaymentMetrics(method));
    }

    @GetMapping("/audits/dashboard")
    public ResponseEntity<AuditDashboardResponse> getAuditDashboard() {
        log.info("START: Fetching audit dashboard");
        return ResponseEntity.ok(reportService.getAuditDashboard());
    }

    @GetMapping("/audits/completed")
    public ResponseEntity<List<AuditDto>> getCompletedAudits() {
        log.info("START: Fetching completed audits");
        return ResponseEntity.ok(reportService.getCompletedAudits());
    }

    @GetMapping("/revenue/dashboard")
    public ResponseEntity<RevenueDashboardResponse> getRevenueDashboard(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String taxpayerType) {
        log.info("START: Revenue Dashboard request for Period: {} and Type: {}", period, taxpayerType);
        return ResponseEntity.ok(reportService.getRevenueDashboard(period, taxpayerType));
    }

    @GetMapping("/custom/download")
    public ResponseEntity<byte[]> downloadCustomReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String reportType,
            @Valid @NotEmpty(message = "At least one metric must be selected") @RequestParam List<String> metrics) {

        log.info("START: Generating Custom Report [{}]", reportType);
        byte[] reportData = reportService.generateCustomReport(startDate, endDate, reportType, metrics);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"report.csv\"")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(reportData);
    }

    @GetMapping("/payments/all")
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        log.info("START: Fetching all payments for charts");
        return ResponseEntity.ok(reportService.getAllPayments());
    }
}