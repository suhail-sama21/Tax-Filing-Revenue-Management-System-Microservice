package com.cognizant.paymentService.controller;

import com.cognizant.paymentService.dto.requestdto.PaymentRequest;
import com.cognizant.paymentService.dto.responsedto.PaymentMetricsResponse;
import com.cognizant.paymentService.dto.responsedto.PaymentResponseDto;
import com.cognizant.paymentService.dto.responsedto.RevenueDashboardResponse;
import com.cognizant.paymentService.entity.entityEnum.PaymentMethod;
import com.cognizant.paymentService.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/pay")
    public PaymentResponseDto makePayment(@Valid @RequestBody PaymentRequest request) {
        log.info("START: Initiating payment for Filing ID: {}", request.getFilingId());
        PaymentResponseDto response = paymentService.makePayment(
                request.getFilingId(), request.getMethod(), request.getAmount(), request.getStatus());
        log.info("END: Payment processed | Payment ID: {} | Status: {}", response.getId(), response.getStatus());
        return response;
    }

    @GetMapping("/history/{taxpayerId}")
    public List<PaymentResponseDto> getPaymentHistory(@PathVariable Long taxpayerId) {
        return paymentService.getPaymentsByTaxpayer(taxpayerId);
    }

    @PostMapping("/retry/{oldPaymentId}")
    public PaymentResponseDto retryPayment(
            @PathVariable Long oldPaymentId,
            @RequestParam PaymentMethod newMethod) {
        return paymentService.retryPayment(oldPaymentId, newMethod);
    }

    @GetMapping("/metrics")
    public ResponseEntity<PaymentMetricsResponse> getPaymentMetrics(
            @RequestParam(required = false) String method) {
        return ResponseEntity.ok(paymentService.getMetrics(method));
    }

    @GetMapping("/revenue")
    public RevenueDashboardResponse getRevenueDashboard(
            @RequestParam(required = false) String period,
            @RequestParam(required = false) String taxpayerType) {

        log.info("START: Fetching payment-service revenue for Period: {} and Type: {}", period, taxpayerType);
        return paymentService.getRevenueDashboard(period, taxpayerType);
    }

    @GetMapping("/all")
    public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
        log.info("Received request for all payment records");
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{paymentId}")
    public PaymentResponseDto getPaymentById(@PathVariable Long paymentId) {
        log.info("START: Fetching payment ID: {}", paymentId);
        return paymentService.getPaymentById(paymentId);
    }
}