package com.cognizant.paymentService.service.imp;

import com.cognizant.paymentService.client.TaxFilingClient;
import com.cognizant.paymentService.client.TaxpayerClient;
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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final RevenueRecordRepository revenueRecordRepository;
    private final TaxFilingClient taxFilingClient;
    private final TaxpayerClient taxpayerClient;

    @Override
    @Transactional
    public PaymentResponseDto makePayment(Long filingId, PaymentMethod method, BigDecimal amount, StatusBasic status) {
        log.info("Processing payment for filing: {}", filingId);

        // 1. Fetch Filing via Feign to get the Taxpayer ID securely!
        TaxFilingDto filing;
        try {
            filing = taxFilingClient.getFilingById(filingId);
        } catch (Exception e) {
            throw new NoSuchElementException("Filing ID " + filingId + " not found");
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
                .orElseThrow(() -> new NoSuchElementException("Payment not found with ID: " + oldPaymentId));

        if (oldPayment.getStatus() != StatusBasic.Failed) {
            throw new IllegalArgumentException("Only failed payments can be retried");
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
    public List<PaymentResponseDto> getAllPayments() {
        log.info("Fetching all payments for custom report");
        List<Payment> payments = paymentRepository.findAll();

        return payments.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentMetricsResponse getMetrics(String method) {
        List<Payment> payments;
        if (method != null && !method.isEmpty()) {
            try {
                PaymentMethod paymentMethod = PaymentMethod.valueOf(method.toUpperCase());
                payments = paymentRepository.findByMethod(paymentMethod);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid Payment Method: " + method);
            }
        } else {
            payments = paymentRepository.findAll();
        }

        long success = payments.stream()
                .filter(p -> p.getStatus() == StatusBasic.Completed)
                .count();

        long failed = payments.stream()
                .filter(p -> p.getStatus() == StatusBasic.Failed)
                .count();

        return PaymentMetricsResponse.builder()
                .successfulTransactions(success)
                .failedTransactions(failed)
                .totalTransactions((long) payments.size())
                .build();
    }

    @Override
    public RevenueDashboardResponse getRevenueDashboard(String period, String taxpayerType) {
        List<RevenueRecord> records = revenueRecordRepository.findAll();

        // 1. Filter by Period (using createdAt timestamp)
        if (period != null && !period.isEmpty()) {
            records = records.stream()
                    .filter(r -> r.getCreatedAt().toString().contains(period))
                    .collect(Collectors.toList());
        }

        // 2. Filter by TaxpayerType (calling TaxpayerService)
        if (taxpayerType != null && !taxpayerType.isEmpty()) {
            records = records.stream()
                    .filter(r -> {
                        try {
                            var taxpayer = taxpayerClient.getTaxpayerTypeById(r.getTaxpayerId());
                            return taxpayerType.equalsIgnoreCase(taxpayer);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }

        BigDecimal total = records.stream()
                .map(RevenueRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return RevenueDashboardResponse.builder()
                .revenueCollected(total)
                .outstandingPayments(paymentRepository.sumOutstandingPayments())
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
                .orElseThrow(() -> new NoSuchElementException("Payment not found with ID: " + paymentId));
        return mapToDto(payment);
    }
}