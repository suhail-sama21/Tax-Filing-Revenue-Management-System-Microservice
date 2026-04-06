package com.cognizant.paymentService.dto.responsedto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentMetricsResponse {
    private long successfulTransactions;
    private long failedTransactions;
    private long totalTransactions;
}