package com.cognizant.taxease.reportingService.client;

import com.cognizant.taxease.reportingService.dto.PaymentMetricsResponse;
import com.cognizant.taxease.reportingService.dto.RevenueDashboardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "payment-service",configuration = FeignClientInterceptor.class)
public interface PaymentClient {
    @GetMapping("/api/payments/metrics")
    PaymentMetricsResponse getPaymentMetrics();

    @GetMapping("/api/payments/revenue")
    RevenueDashboardResponse getRevenueDashboard();
}