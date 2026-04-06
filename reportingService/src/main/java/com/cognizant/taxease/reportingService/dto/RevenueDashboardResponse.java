package com.cognizant.taxease.reportingService.dto;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class RevenueDashboardResponse {
    private BigDecimal revenueCollected;
    private BigDecimal outstandingPayments;
}