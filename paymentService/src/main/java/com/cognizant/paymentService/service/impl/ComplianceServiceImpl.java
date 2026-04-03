package com.cognizant.paymentService.service.impl;

import com.cognizant.taxease.dao.*;
import com.cognizant.taxease.dto.responsedto.ComplianceResponse;
import com.cognizant.taxease.dto.requestdto.CreateComplianceRequest;
import com.cognizant.taxease.dto.requestdto.UpdateComplianceRequest;
import com.cognizant.taxease.entity.*;
import com.cognizant.taxease.entity.entityEnum.ComplianceType;
import com.cognizant.taxease.entity.entityEnum.StatusBasic;
import com.cognizant.taxease.service.AuditLogService;
import com.cognizant.taxease.service.ComplianceService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ComplianceServiceImpl implements ComplianceService {

    private final ComplianceRecordRepository complianceRecordRepository;
    private final TaxpayerRepository taxpayerRepository;
    private final TaxFilingRepository taxFilingRepository;
    private final PaymentRepository paymentRepository;
    private final AuditLogService auditLogService;

    private final AuditRepository auditRepository;
    private final UserRepository userRepository;

    @Override
    public List<ComplianceResponse> getAllCompliance() {
        return complianceRecordRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    @Override
    public ComplianceResponse createCompliance(CreateComplianceRequest request) {
        ComplianceRecord record = new ComplianceRecord();

        Taxpayer taxpayer = taxpayerRepository.findById(request.getTaxpayerId())
                .orElseThrow(() -> new NoSuchElementException("Taxpayer not found"));
        record.setTaxpayer(taxpayer);

        if (request.getType() == ComplianceType.Filing) {
            if (request.getFilingId() == null) {
                throw new IllegalArgumentException("filingId is required when type is Filing");
            }
            TaxFiling filing = taxFilingRepository.findById(request.getFilingId())
                    .orElseThrow(() -> new NoSuchElementException("Filing not found"));

            record.setFiling(filing);
            record.setPayment(null);

        } else if (request.getType() == ComplianceType.Payment) {
            if (request.getPaymentId() == null) {
                throw new IllegalArgumentException("paymentId is required when type is Payment");
            }
            Payment payment = paymentRepository.findById(request.getPaymentId())
                    .orElseThrow(() -> new NoSuchElementException("Payment not found"));

            record.setPayment(payment);
            record.setFiling(null);

        } else {
            throw new IllegalArgumentException("Unsupported compliance type: " + request.getType());
        }

        record.setType(request.getType());
        record.setResult(request.getResult());
        record.setNotes(request.getNotes());

        // Save the record to the database
        ComplianceRecord savedRecord = complianceRecordRepository.save(record);
        auditLogService.record("COMPLIANCE_CREATE", "compliance_records/" + savedRecord.getId());
        // Map the saved entity to your DTO to prevent infinite JSON recursion
        return mapToResponse(savedRecord);

    }
    private ComplianceResponse mapToResponse(ComplianceRecord record) {
        return ComplianceResponse.builder()
                .id(record.getId())
                .taxpayerId(record.getTaxpayer() != null ? record.getTaxpayer().getId() : null)
                .filingId(record.getFiling() != null ? record.getFiling().getId() : null)
                .paymentId(record.getPayment() != null ? record.getPayment().getId() : null)
                .type(record.getType())
                .result(record.getResult())
                .date(record.getDate())
                .notes(record.getNotes())
                .createdAt(record.getCreatedAt())
                .build();
    }

    @Override
    public ComplianceResponse getComplianceById(Long id) {
        ComplianceRecord record = complianceRecordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compliance not found"));
        auditLogService.record("COMPLIANCE_VIEW", "compliance_records/" + id);
        return mapToResponse(record);
    }


    @Override
    public ComplianceResponse updateCompliance(Long id, UpdateComplianceRequest request) {

        ComplianceRecord record = complianceRecordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compliance not found"));

        if (request.getResult() != null && !request.getResult().isBlank()) {
            record.setResult(request.getResult());

            if ("Non-Compliant".equalsIgnoreCase(request.getResult())) {

                User officer = userRepository.findById(1L)
                        .orElseThrow(() -> new RuntimeException("Officer not found"));

                Audit audit = Audit.builder()
                        .officer(officer)
                        .scope("Compliance Check for ID: " + record.getId())
                        .findings(record.getNotes() != null ? record.getNotes() : "Auto-detected issue")
                        .status(StatusBasic.Active)
                        .build();

                Audit updatedRecord=auditRepository.save(audit);
                auditLogService.record("COMPLIANCE_UPDATE", "compliance_records/" + updatedRecord.getId());
            }
        }

        if (request.getNotes() != null && !request.getNotes().isBlank()) {
            record.setNotes(request.getNotes());
        }

        ComplianceRecord updatedRecord = complianceRecordRepository.save(record);
        return mapToResponse(updatedRecord);
    }

    @Override
    public List<ComplianceResponse> getComplianceByTaxpayerId(Long taxpayerId) {
        auditLogService.record("COMPLIANCE_LIST_VIEW", "taxpayer/" + taxpayerId + "/compliance");
        return complianceRecordRepository.findByTaxpayer_Id(taxpayerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    public List<ComplianceResponse> getByResult(String result){
        return complianceRecordRepository.findByResultIgnoreCase(result)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
}