package com.cognizant.paymentService.service;

import com.cognizant.paymentService.dto.responsedto.PaymentMetricsResponse;
import com.cognizant.paymentService.dto.responsedto.PaymentResponseDto;
import com.cognizant.paymentService.dto.responsedto.RevenueDashboardResponse;
import com.cognizant.paymentService.entity.entityEnum.PaymentMethod;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {
    PaymentResponseDto makePayment(Long filingId, PaymentMethod method, BigDecimal amount, StatusBasic status);
    List<PaymentResponseDto> getPaymentsByTaxpayer(Long taxpayerId);
    PaymentResponseDto retryPayment(Long oldPaymentId, PaymentMethod newMethod);

    // Admin dashboard methods
    PaymentMetricsResponse getPaymentMetrics();
    RevenueDashboardResponse getRevenueDashboard();
}