package com.cognizant.paymentService.dto.requestdto;

import com.cognizant.taxease.entity.entityEnum.PaymentMethod;
import com.cognizant.taxease.entity.entityEnum.StatusBasic;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {

    @NotNull(message = "Filing ID is required to process a payment")
    private Long filingId;

    @NotNull(message = "Payment method is required")
    private PaymentMethod method;

    @NotNull(message = "Payment amount is required")
    @Positive(message = "Payment amount must be greater than zero")
    private BigDecimal amount;

    // Optional: The frontend might send this, but usually, your backend
    // Service layer should set the initial status (e.g., StatusBasic.PENDING)
    // automatically so users can't force a "COMPLETED" status themselves!
    private StatusBasic status;
}