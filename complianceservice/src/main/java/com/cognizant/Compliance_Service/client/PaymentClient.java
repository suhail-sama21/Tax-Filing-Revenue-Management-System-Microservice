package com.cognizant.Compliance_Service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "payment-service")
public interface PaymentClient {
    @GetMapping("/api/payments/{paymentId}")
    Object verifyPaymentExists(@PathVariable("paymentId") Long paymentId);
}