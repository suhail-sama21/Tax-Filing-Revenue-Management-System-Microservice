package com.cognizant.taxFilingService.dto.requestdto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TaxFilingRequestDTO {
    @NotNull(message = "Taxpayer ID is required")
    private Long taxpayerId;

    @NotBlank(message = "Filing period is required")
    @Pattern(regexp = "^FY\\d{4}-\\d{2}$", message = "Format must be FYYYYY-YY (e.g., FY2025-26)")
    private String period;

    @NotNull(message = "Declared amount cannot be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Amount cannot be negative")
    private BigDecimal amountDeclared;
}