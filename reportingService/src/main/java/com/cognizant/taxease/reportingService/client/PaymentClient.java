package com.cognizant.taxease.reportingService.client;

import com.cognizant.taxease.reportingService.dto.PaymentMetricsResponse;
import com.cognizant.taxease.reportingService.dto.RevenueDashboardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payment-service",configuration = FeignClientInterceptor.class)
public abstract class PaymentClient {
    @GetMapping("/api/payments/metrics")
    public abstract PaymentMetricsResponse getPaymentMetrics();

    @GetMapping("/api/payments/revenue")
    public abstract RevenueDashboardResponse getRevenueDashboard();
}