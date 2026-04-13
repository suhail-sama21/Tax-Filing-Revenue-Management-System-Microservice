package com.cognizant.reportingService.client;

import com.cognizant.reportingService.dto.PaymentMetricsResponse;
import com.cognizant.reportingService.dto.RevenueDashboardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payment-service",configuration = FeignClientInterceptor.class)
public interface PaymentClient {
    @GetMapping("/api/payments/metrics")
    public  PaymentMetricsResponse getPaymentMetrics();

    @GetMapping("/api/payments/revenue")
    public RevenueDashboardResponse getRevenueDashboard();
}