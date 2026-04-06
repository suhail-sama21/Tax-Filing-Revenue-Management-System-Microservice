package com.cognizant.taxease.reportingService.client;

import com.cognizant.taxease.reportingService.dto.ComplianceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "compliance-service")
public interface ComplianceClient {
    @GetMapping("/api/compliance")
    List<ComplianceDto> getAllCompliance();

    @GetMapping("/api/compliance/result/{result}")
    List<ComplianceDto> getByResult(@PathVariable("result") String result);
}