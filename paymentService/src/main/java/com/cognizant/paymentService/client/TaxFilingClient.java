package com.cognizant.paymentService.client;

import com.cognizant.paymentService.dto.requestdto.TaxFilingDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "tax-filing-service")
public interface TaxFilingClient {
    // We just need a simple DTO to catch the response
    @GetMapping("/api/filings/{filingId}")
    TaxFilingDto getFilingById(@PathVariable("filingId") Long filingId);
}