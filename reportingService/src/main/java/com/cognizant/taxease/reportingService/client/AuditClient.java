package com.cognizant.taxease.reportingService.client;

import com.cognizant.taxease.reportingService.dto.AuditDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "audit-service",configuration = FeignClientInterceptor.class)
public interface AuditClient {
    @GetMapping("/api/audit")
    List<AuditDto> getAllAudits();
}