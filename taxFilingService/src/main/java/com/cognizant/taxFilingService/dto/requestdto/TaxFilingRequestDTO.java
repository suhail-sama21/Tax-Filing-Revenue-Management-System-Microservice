package com.cognizant.taxFilingService.dto.requestdto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TaxFilingRequestDTO {
    @NotNull(message = "Taxpayer ID is required")
    private Long taxpayerId;

    @NotBlank(message = "Filing period is required")
    private String period;

    @NotNull(message = "Declared amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = true)
    private BigDecimal amountDeclared;
}