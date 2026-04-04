package com.cognizant.taxpayerService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Must match the application.name of your Audit Service in Eureka
@FeignClient(name = "AUDIT-SERVICE")
public interface AuditServiceClient {

    // Assuming your Audit Service has an endpoint to create logs
    @PostMapping("/api/audit/record")
    void recordLog(@RequestParam("action") String action, @RequestParam("details") String details);
}