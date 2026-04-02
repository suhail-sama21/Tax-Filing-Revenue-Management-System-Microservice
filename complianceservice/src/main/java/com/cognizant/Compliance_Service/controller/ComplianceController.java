package com.cognizant.Compliance_Service.controller;

import com.cognizant.Compliance_Service.dto.ComplianceResponse;
import com.cognizant.Compliance_Service.dto.CreateComplianceRequest;
import com.cognizant.Compliance_Service.dto.UpdateComplianceRequest;
import com.cognizant.Compliance_Service.service.ComplianceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
public class ComplianceController {

    private final ComplianceService complianceService;

    @PostMapping
    public ComplianceResponse createCompliance(@RequestBody CreateComplianceRequest request) {
        return complianceService.createCompliance(request);
    }

    @GetMapping
    public List<ComplianceResponse> getAllCompliance() {
        return complianceService.getAllCompliance();
    }

    @GetMapping("/{id}")
    public ComplianceResponse getComplianceById(@PathVariable Long id) {
        return complianceService.getComplianceById(id);
    }

    @PutMapping("/{id}")
    public ComplianceResponse updateCompliance(@PathVariable Long id,
                                               @RequestBody UpdateComplianceRequest request) {
        return complianceService.updateCompliance(id, request);
    }

    @GetMapping("/taxpayer/{taxpayerId}")
    public List<ComplianceResponse> getComplianceByTaxpayerId(@PathVariable Long taxpayerId) {
        return complianceService.getComplianceByTaxpayerId(taxpayerId);
    }

    @GetMapping("/result/{result}")
    public List<ComplianceResponse> getByResult(@PathVariable String result) {
        return complianceService.getByResult(result);
    }
}