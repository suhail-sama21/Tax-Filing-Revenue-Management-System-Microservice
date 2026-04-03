package com.cognizant.taxFilingService.dto.requestdto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxFilingRequestDTO {

    @NotNull(message = "Taxpayer ID is required")
    private Long taxpayerId;

    @NotBlank(message = "Filing period is required (e.g., Q1-2026 or 2025-Annual)")
    private String period;

    @NotNull(message = "Declared amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Declared amount must be zero or a positive number")
    private BigDecimal amountDeclared;
}