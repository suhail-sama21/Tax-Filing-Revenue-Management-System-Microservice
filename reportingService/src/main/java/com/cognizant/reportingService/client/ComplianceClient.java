package com.cognizant.reportingService.client;

import com.cognizant.reportingService.dto.ComplianceDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "compliance-service",configuration = FeignClientInterceptor.class)
public interface ComplianceClient {
    @GetMapping("/api/compliance")
    List<ComplianceDto> getAllCompliance();

    @GetMapping("/api/compliance/result/{result}")
    List<ComplianceDto> getByResult(@PathVariable("result") String result);
}