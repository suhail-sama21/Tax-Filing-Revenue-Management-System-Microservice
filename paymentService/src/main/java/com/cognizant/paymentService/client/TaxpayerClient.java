package com.cognizant.paymentService.client;

import com.cognizant.taxpayerService.dto.TaxpayerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "TAXPAYER-SERVICE",configuration = FeignClientInterceptor.class)
public interface TaxpayerClient {
    @GetMapping("/api/taxpayers/{id}")
    String getTaxpayerById(@PathVariable("id") Long id);
}
