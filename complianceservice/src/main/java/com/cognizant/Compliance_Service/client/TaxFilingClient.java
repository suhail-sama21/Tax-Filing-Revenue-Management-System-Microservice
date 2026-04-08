package com.cognizant.Compliance_Service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tax-filing-service",configuration = FeignClientInterceptor.class)
public interface TaxFilingClient {
    // Assuming you will add a simple GET by ID endpoint to your Filing service!
    @GetMapping("/api/filings/{filingId}")
    Object verifyFilingExists(@PathVariable("filingId") Long filingId);
}