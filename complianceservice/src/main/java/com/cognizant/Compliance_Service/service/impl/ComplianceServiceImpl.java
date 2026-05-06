package com.cognizant.Compliance_Service.service.impl;

import com.cognizant.Compliance_Service.client.PaymentClient;
import com.cognizant.Compliance_Service.client.TaxFilingClient;
import com.cognizant.Compliance_Service.client.TaxpayerClient;
import com.cognizant.Compliance_Service.dto.ComplianceDashboardResponse;
import com.cognizant.Compliance_Service.dto.ComplianceResponse;
import com.cognizant.Compliance_Service.dto.CreateComplianceRequest;
import com.cognizant.Compliance_Service.dto.UpdateComplianceRequest;
import com.cognizant.Compliance_Service.entity.ComplianceRecord;
import com.cognizant.Compliance_Service.repository.ComplianceRecordRepository;
import com.cognizant.Compliance_Service.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import com.cognizant.Compliance_Service.dto.ComplianceDashboardResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class ComplianceServiceImpl implements ComplianceService {

    private final ComplianceRecordRepository complianceRecordRepository;

    // Injecting our Feign Clients
    private final TaxpayerClient taxpayerClient;
    private final TaxFilingClient taxFilingClient;
    private final PaymentClient paymentClient;

    @Override
    public ComplianceDashboardResponse getDashboardSummary() {

        long totalChecks = complianceRecordRepository.count();

        long pendingReviews = complianceRecordRepository.countByResultIgnoreCase("Pending");

        long nonCompliant = complianceRecordRepository.countByResultIgnoreCase("Non-Compliant");

        long compliant = complianceRecordRepository.countByResultIgnoreCase("Compliant");

        double systemHealth = 0.0;

        if (totalChecks > 0) {
            systemHealth = ((double) compliant / totalChecks) * 100;
        }

        return ComplianceDashboardResponse.builder()
                .totalChecks(totalChecks)
                .pendingReviews(pendingReviews)
                .nonCompliant(nonCompliant)
                .compliant(compliant)
                .systemHealth(systemHealth)
                .build();
    }

    @Override
    public List<ComplianceResponse> getAllCompliance() {
        return complianceRecordRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ComplianceResponse createCompliance(CreateComplianceRequest request) {
        log.info("Creating compliance record for Taxpayer ID: {}", request.getTaxpayerId());

        // 1. Verify Taxpayer Exists via Feign
        try {
            taxpayerClient.verifyTaxpayerExists(request.getTaxpayerId());
        } catch (Exception e) {
            throw new NoSuchElementException("Taxpayer not found in Taxpayer Service");
        }

        // 2. Validate Type and Verify specific entity exists via Feign
        if ("Filing".equalsIgnoreCase(String.valueOf(request.getType()))) {
            if (request.getFilingId() == null) {
                throw new IllegalArgumentException("filingId is required when type is Filing");
            }
            try {
                taxFilingClient.verifyFilingExists(request.getFilingId());
            } catch (Exception e) {
                throw new NoSuchElementException("Filing not found in Tax Filing Service");
            }
            request.setPaymentId(null); // Ensure clean data

        } else if ("Payment".equalsIgnoreCase(String.valueOf(request.getType()))) {
            if (request.getPaymentId() == null) {
                throw new IllegalArgumentException("paymentId is required when type is Payment");
            }
            try {
                paymentClient.verifyPaymentExists(request.getPaymentId());
            } catch (Exception e) {
                throw new NoSuchElementException("Payment not found in Payment Service");
            }
            request.setFilingId(null); // Ensure clean data

        } else {
            throw new IllegalArgumentException("Unsupported compliance type: " + request.getType());
        }

        ComplianceRecord record = ComplianceRecord.builder()
                .taxpayerId(request.getTaxpayerId())
                .filingId(request.getFilingId())
                .paymentId(request.getPaymentId())
                .type(request.getType())
                .result(request.getResult())
                .notes(request.getNotes())
                .date(LocalDate.now())
                .build();

        ComplianceRecord saved = complianceRecordRepository.save(record);
        log.info("Compliance record created successfully with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    @Override
    public ComplianceResponse getComplianceById(Long id) {
        ComplianceRecord record = complianceRecordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compliance not found"));
        return mapToResponse(record);
    }

    @Override
    public ComplianceResponse updateCompliance(Long id, UpdateComplianceRequest request) {
        log.info("Updating compliance record ID: {}", id);
        ComplianceRecord record = complianceRecordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compliance not found"));

        if (request.getResult() != null && !request.getResult().isBlank()) {
            record.setResult(request.getResult());

            // Monolith Logic Ported: Trigger Audit on Non-Compliant
            if ("Non-Compliant".equalsIgnoreCase(request.getResult())) {
                log.warn("Record {} marked as Non-Compliant. Triggering Audit creation.", id);

                // TODO: Call AuditServiceClient.createAudit(...) here once the Audit Service is built!
                // Example:
                // auditClient.createAutoAudit(AuditRequest.builder()
                //      .scope("Compliance Check for ID: " + record.getId())
                //      .findings(request.getNotes() != null ? request.getNotes() : "Auto-detected issue")
                //      .build());
            }
        }

        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            record.setNotes(request.getNotes());
        }

        ComplianceRecord updated = complianceRecordRepository.save(record);
        return mapToResponse(updated);
    }

    @Override
    public List<ComplianceResponse> getComplianceByTaxpayerId(Long taxpayerId) {
        return complianceRecordRepository.findByTaxpayerId(taxpayerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<ComplianceResponse> getByResult(String result) {
        return complianceRecordRepository.findByResultIgnoreCase(result)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private ComplianceResponse mapToResponse(ComplianceRecord record) {
        return ComplianceResponse.builder()
                .id(record.getId())
                .taxpayerId(record.getTaxpayerId())
                .filingId(record.getFilingId())
                .paymentId(record.getPaymentId())
                .type(record.getType())
                .result(record.getResult())
                .date(record.getDate())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .build();
    }
}