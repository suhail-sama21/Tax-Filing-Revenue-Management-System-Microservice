package com.cognizant.paymentService.service;

import com.cognizant.paymentService.dto.responsedto.PaymentMetricsResponse;
import com.cognizant.paymentService.dto.responsedto.PaymentResponseDto;
import com.cognizant.paymentService.dto.responsedto.RevenueDashboardResponse;
import com.cognizant.paymentService.entity.Payment;
import com.cognizant.paymentService.entity.entityEnum.PaymentMethod;
import com.cognizant.paymentService.entity.entityEnum.StatusBasic;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    RevenueDashboardResponse getRevenueDashboard(String period, String taxpayerType);
    PaymentMetricsResponse getMetrics(String method);
    PaymentResponseDto retryPayment(Long oldPaymentId, PaymentMethod newMethod);
    List<PaymentResponseDto> getPaymentsByTaxpayer(Long taxpayerId);
    PaymentResponseDto makePayment(Long filingId, PaymentMethod method, BigDecimal amount, StatusBasic status);
    PaymentResponseDto getPaymentById(Long paymentId);
    List<PaymentResponseDto> getAllPayments();
}