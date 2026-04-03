package com.cognizant.taxFilingService.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxFilingResponseDTO {
    private Long id;
    private Long taxpayerId;
    private String period;
    private BigDecimal amountDeclared;
    private String status; // Kept as String for the frontend
    private Instant submittedDate;
}