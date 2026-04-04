package com.cognizant.paymentService.service.impl;

import com.cognizant.taxease.dao.PaymentRepository;
import com.cognizant.taxease.dao.RevenueRecordRepository;
import com.cognizant.taxease.dao.TaxFilingRepository;
import com.cognizant.taxease.dto.responsedto.PaymentMetricsResponse;
import com.cognizant.taxease.dto.responsedto.PaymentResponseDto;
import com.cognizant.taxease.dto.responsedto.RevenueDashboardResponse;
import com.cognizant.taxease.entity.Payment;
import com.cognizant.taxease.entity.RevenueRecord;
import com.cognizant.taxease.entity.TaxFiling;
import com.cognizant.taxease.entity.entityEnum.PaymentMethod;
import com.cognizant.taxease.entity.entityEnum.StatusBasic;
import com.cognizant.taxease.service.AuditLogService;
import com.cognizant.taxease.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TaxFilingRepository taxFilingRepository;

    @Autowired
    private RevenueRecordRepository revenueRecordRepository;

    @Autowired
    private AuditLogService auditLogService;

    @Override
    @Transactional
    public PaymentResponseDto makePayment(Long filingId, PaymentMethod method, BigDecimal amount, StatusBasic status) {
        TaxFiling filing = taxFilingRepository.findById(filingId)
                .orElseThrow(() -> new RuntimeException("Filing not found"));

        Payment payment = Payment.builder()
                .filing(filing)
                .method(method)
                .amount(amount)
                .status(status)
                .build();

        payment = paymentRepository.save(payment);

        if (status == StatusBasic.Completed) {
            RevenueRecord revenueRecord = RevenueRecord.builder()
                    .taxpayer(filing.getTaxpayer())
                    .payment(payment)
                    .amount(amount)
                    .status(StatusBasic.Completed)
                    .build();
            revenueRecordRepository.save(revenueRecord);

            auditLogService.record("PAYMENT_CREATE", "payments/" + payment.getId());
        }

        return mapToDto(payment);
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByTaxpayer(Long taxpayerId) {
        List<Payment> payments = paymentRepository.findByFiling_Taxpayer_Id(taxpayerId);
        return payments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PaymentResponseDto retryPayment(Long oldPaymentId, PaymentMethod newMethod) {
        Payment oldPayment = paymentRepository.findById(oldPaymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (oldPayment.getStatus() != StatusBasic.Failed) {
            throw new RuntimeException("Only failed payments can be retried");
        }

        auditLogService.record("PAYMENT_RETRY", "payments/" + oldPaymentId);
        return makePayment(
                oldPayment.getFiling().getId(),
                newMethod,
                oldPayment.getAmount(),
                StatusBasic.Completed // Assuming success for the retry
        );
    }

    @Override
    public PaymentMetricsResponse getPaymentMetrics() {
        long successful = paymentRepository.countByStatus(StatusBasic.Completed);
        long failed = paymentRepository.countByStatus(StatusBasic.Failed);
        long total = paymentRepository.count();

        return PaymentMetricsResponse.builder()
                .successfulTransactions(successful)
                .failedTransactions(failed)
                .totalTransactions(total)
                .build();
    }

    @Override
    public RevenueDashboardResponse getRevenueDashboard() {
        BigDecimal collected = revenueRecordRepository.sumCollectedRevenue();
        BigDecimal outstanding = paymentRepository.sumOutstandingPayments();

        return RevenueDashboardResponse.builder()
                .revenueCollected(collected != null ? collected : BigDecimal.ZERO)
                .outstandingPayments(outstanding != null ? outstanding : BigDecimal.ZERO)
                .build();
    }

    // Helper method to map Payment entity to PaymentResponseDto
    private PaymentResponseDto mapToDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .filingId(payment.getFiling().getId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .date(payment.getDate())
                .build();
    }
}