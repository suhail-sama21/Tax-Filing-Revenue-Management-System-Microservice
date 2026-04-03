package com.cognizant.paymentService.controller;

import com.cognizant.paymentService.dto.requestdto.PaymentRequest;
import com.cognizant.paymentService.dto.responsedto.PaymentMetricsResponse;
import com.cognizant.paymentService.dto.responsedto.PaymentResponseDto;
import com.cognizant.paymentService.dto.responsedto.RevenueDashboardResponse;
import com.cognizant.paymentService.entity.entityEnum.PaymentMethod;
import com.cognizant.paymentService.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@Slf4j
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/pay")
    public PaymentResponseDto makePayment(@RequestBody PaymentRequest request) {
        log.info("START: Initiating payment for Filing ID: {} | Amount: {}", request.getFilingId(), request.getAmount());
        PaymentResponseDto response = paymentService.makePayment(request.getFilingId(), request.getMethod(), request.getAmount(), request.getStatus());
        log.info("END: Payment processed | Payment ID: {} | Status: {}", response.getId(), response.getStatus());
        return response;
    }

    @GetMapping("/history/{taxpayerId}")
    public List<PaymentResponseDto> getPaymentHistory(@PathVariable Long taxpayerId) {
        log.info("START: Fetching payment history for taxpayer {}", taxpayerId);
        List<PaymentResponseDto> response = paymentService.getPaymentsByTaxpayer(taxpayerId);
        log.info("END: Retrieved {} payment records", response.size());
        return response;
    }

    @PostMapping("/retry/{oldPaymentId}")
    public PaymentResponseDto retryPayment(
            @PathVariable Long oldPaymentId,
            @RequestParam PaymentMethod newMethod) {
        log.info("START: Retrying payment for old ID: {} | New Method: {}", oldPaymentId, newMethod);
        PaymentResponseDto response = paymentService.retryPayment(oldPaymentId, newMethod);
        log.info("END: Retry payment processed | New Payment ID: {}", response.getId());
        return response;
    }

    @GetMapping("/metrics")
    public PaymentMetricsResponse getPaymentMetrics() {
        log.info("START: Fetching payment metrics");
        return paymentService.getPaymentMetrics();
    }

    @GetMapping("/revenue")
    public RevenueDashboardResponse getRevenueDashboard() {
        log.info("START: Fetching revenue dashboard");
        return paymentService.getRevenueDashboard();
    }
}