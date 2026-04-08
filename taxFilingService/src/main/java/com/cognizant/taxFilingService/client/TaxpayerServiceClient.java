package com.cognizant.taxFilingService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// This connects to your Taxpayer Service!
@FeignClient(name = "taxpayer-service",configuration = FeignClientInterceptor.class)
public interface TaxpayerServiceClient {

    // We just need to hit this endpoint. If it returns 200 OK, the taxpayer exists!
    // If it returns 404/500, Feign will throw an exception and stop the filing.
    @GetMapping("/api/taxpayers/user/{userId}/full-profile")
    Object verifyTaxpayerExists(@PathVariable("userId") Long userId);
}