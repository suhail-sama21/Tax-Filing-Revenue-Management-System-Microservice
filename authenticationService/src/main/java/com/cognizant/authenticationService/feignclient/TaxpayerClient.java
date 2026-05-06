package com.cognizant.authenticationService.feignclient;

import com.cognizant.authenticationService.dto.TaxpayerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "TAXPAYER-SERVICE",configuration = FeignClientInterceptor.class)
public interface TaxpayerClient {
    @PostMapping("/api/taxpayers/profile")
    void createProfile(@RequestParam("email") String email, @RequestParam(value = "type", required = false) String type);
}
