package com.cognizant.taxFilingService.dto.responsedto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class TaxFilingResponseDTO {
    private Long id;
    private Long taxpayerId;
    private String period;
    private BigDecimal amountDeclared;
    private String status;
    private Long officerId;
    private Instant submittedDate;
}