package com.cognizant.reportingService.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RevenueDashboardResponse {
    private BigDecimal revenueCollected;
    private BigDecimal outstandingPayments;
}