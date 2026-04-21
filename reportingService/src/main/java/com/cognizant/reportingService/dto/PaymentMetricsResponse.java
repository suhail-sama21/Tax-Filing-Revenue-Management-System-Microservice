package com.cognizant.reportingService.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMetricsResponse {
    private long successfulTransactions;
    private long failedTransactions;
    private long totalTransactions;
}