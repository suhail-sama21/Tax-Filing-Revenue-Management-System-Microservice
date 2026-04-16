package com.cognizant.reportingService.client;

import com.cognizant.paymentService.dto.responsedto.PaymentResponseDto;
import com.cognizant.reportingService.dto.PaymentMetricsResponse;
import com.cognizant.reportingService.dto.RevenueDashboardResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "payment-service", configuration = FeignClientInterceptor.class)
public interface PaymentClient {

    // Updated to accept the method parameter
    @GetMapping("/api/payments/metrics")
    PaymentMetricsResponse getPaymentMetrics(@RequestParam(required = false) String method);

    @GetMapping("/api/payments/revenue")
    RevenueDashboardResponse getRevenueDashboard(
            @RequestParam(name = "period", required = false) String period,
            @RequestParam(name = "taxpayerType", required = false) String taxpayerType
    );

    @GetMapping("/api/payments/all")
    List<PaymentResponseDto> getAllPayments();
}