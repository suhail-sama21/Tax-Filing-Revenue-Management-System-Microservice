package com.cognizant.paymentService.dto.requestdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaxFilingDto {
    // Lombok's @Data will automatically generate the getTaxpayerId() method for this field
    private Long taxpayerId;
}