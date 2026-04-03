package com.cognizant.Compliance_Service.service.impl;

import com.cognizant.Compliance_Service.dto.ComplianceResponse;
import com.cognizant.Compliance_Service.dto.CreateComplianceRequest;
import com.cognizant.Compliance_Service.dto.UpdateComplianceRequest;
import com.cognizant.Compliance_Service.entity.ComplianceRecord;
import com.cognizant.Compliance_Service.repository.ComplianceRecordRepository;
import com.cognizant.Compliance_Service.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ComplianceServiceImpl implements ComplianceService {

    private final ComplianceRecordRepository complianceRecordRepository;

    @Override
    public List<ComplianceResponse> getAllCompliance() {
        return complianceRecordRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public ComplianceResponse createCompliance(CreateComplianceRequest request) {
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
        return mapToResponse(saved);
    }

    @Override
    public ComplianceResponse getComplianceById(Long id) {
        ComplianceRecord record = complianceRecordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compliance not found"));
        return mapToResponse(record);
    }
// compliance to audit ->
    @Override
    public ComplianceResponse updateCompliance(Long id, UpdateComplianceRequest request) {
        ComplianceRecord record = complianceRecordRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Compliance not found"));

        if (request.getResult() != null && !request.getResult().isBlank()) {
            record.setResult(request.getResult());
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