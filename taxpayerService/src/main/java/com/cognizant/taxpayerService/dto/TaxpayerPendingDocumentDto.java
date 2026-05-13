package com.cognizant.taxpayerService.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaxpayerPendingDocumentDto {
    private Long userId;
    private String name;
    private String panNumber;
    private String verificationStatus;
}
