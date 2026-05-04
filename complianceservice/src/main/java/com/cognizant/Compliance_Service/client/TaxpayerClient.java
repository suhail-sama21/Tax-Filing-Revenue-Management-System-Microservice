package com.cognizant.Compliance_Service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "taxpayer-service",configuration = FeignClientInterceptor.class)
public interface TaxpayerClient {
    @GetMapping("/api/taxpayers/user/{userId}/full-profile")
    Object verifyTaxpayerExists(@PathVariable("userId") Long userId);
}