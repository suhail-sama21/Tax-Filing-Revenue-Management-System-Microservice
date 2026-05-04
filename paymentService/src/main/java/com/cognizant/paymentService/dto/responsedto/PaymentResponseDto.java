package com.cognizant.paymentService.dto.responsedto;

import com.cognizant.paymentService.entity.entityEnum.PaymentMethod;
import com.cognizant.paymentService.entity.entityEnum.StatusBasic;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    private Long id;
    private Long filingId;
    private Long taxpayerId;
    private BigDecimal amount;
    private PaymentMethod method;
    private StatusBasic status;
    private Instant date;
}