package com.cognizant.paymentService.service.imp;

import com.cognizant.paymentService.client.TaxFilingClient;
import com.cognizant.paymentService.dto.requestdto.*;
import com.cognizant.paymentService.dto.responsedto.*;

import com.cognizant.paymentService.dto.responsedto.PaymentResponseDto;
import com.cognizant.paymentService.entity.Payment;
import com.cognizant.paymentService.entity.RevenueRecord;
import com.cognizant.paymentService.entity.entityEnum.PaymentMethod;
import com.cognizant.paymentService.entity.entityEnum.StatusBasic;
import com.cognizant.paymentService.dao.PaymentRepository;
import com.cognizant.paymentService.dao.RevenueRecordRepository;
import com.cognizant.paymentService.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RevenueRecordRepository revenueRecordRepository;
    private final TaxFilingClient taxFilingClient;

    @Override
    @Transactional
    public PaymentResponseDto makePayment(Long filingId, PaymentMethod method, BigDecimal amount, StatusBasic status) {
        log.info("Processing payment for filing: {}", filingId);

        // 1. Fetch Filing via Feign to get the Taxpayer ID securely!
        TaxFilingDto filing;
        try {
            filing = taxFilingClient.getFilingById(filingId);
        } catch (Exception e) {
            throw new RuntimeException("Filing ID " + filingId + " not found in Tax Filing Service");
        }

        // 2. Save Payment with BOTH IDs
        Payment payment = Payment.builder()
                .filingId(filingId)
                .taxpayerId(filing.getTaxpayerId())
                .method(method)
                .amount(amount)
                .status(status != null ? status : StatusBasic.Completed)
                .build();

        payment = paymentRepository.save(payment);

        // 3. Record Revenue if Successful
        if (payment.getStatus() == StatusBasic.Completed) {
            RevenueRecord revenueRecord = RevenueRecord.builder()
                    .taxpayerId(filing.getTaxpayerId())
                    .payment(payment)
                    .amount(amount)
                    .status(StatusBasic.Completed)
                    .build();
            revenueRecordRepository.save(revenueRecord);
        }

        return mapToDto(payment);
    }

    @Override
    public List<PaymentResponseDto> getPaymentsByTaxpayer(Long taxpayerId) {
        return paymentRepository.findByTaxpayerId(taxpayerId).stream()
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

        // Make a new payment attempt
        return makePayment(
                oldPayment.getFilingId(),
                newMethod,
                oldPayment.getAmount(),
                StatusBasic.Completed
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

    private PaymentResponseDto mapToDto(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .filingId(payment.getFilingId())
                .taxpayerId(payment.getTaxpayerId())
                .amount(payment.getAmount())
                .method(payment.getMethod())
                .status(payment.getStatus())
                .date(payment.getDate())
                .build();
    }
    @Override
    public PaymentResponseDto getPaymentById(Long paymentId) {
        log.info("Fetching payment by ID: {}", paymentId);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        return mapToDto(payment);
    }
}