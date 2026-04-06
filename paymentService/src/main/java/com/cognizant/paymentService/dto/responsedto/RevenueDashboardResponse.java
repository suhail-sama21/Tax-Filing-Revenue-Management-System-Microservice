package com.cognizant.paymentService.dto.responsedto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class RevenueDashboardResponse {
    private BigDecimal revenueCollected;
    private BigDecimal outstandingPayments;
}