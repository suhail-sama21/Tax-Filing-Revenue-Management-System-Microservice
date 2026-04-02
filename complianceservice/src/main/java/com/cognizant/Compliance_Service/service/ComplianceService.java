package com.cognizant.Compliance_Service.service;

import com.cognizant.Compliance_Service.dto.ComplianceResponse;
import com.cognizant.Compliance_Service.dto.CreateComplianceRequest;
import com.cognizant.Compliance_Service.dto.UpdateComplianceRequest;

import java.util.List;

public interface ComplianceService {
    List<ComplianceResponse> getAllCompliance();

    ComplianceResponse createCompliance(CreateComplianceRequest request);

    ComplianceResponse getComplianceById(Long id);

    ComplianceResponse updateCompliance(Long id, UpdateComplianceRequest request);

    List<ComplianceResponse> getComplianceByTaxpayerId(Long taxpayerId);

    List<ComplianceResponse> getByResult(String result);
}