package com.cognizant.taxpayerService.dto;

import lombok.Builder;
import lombok.Data;
import java.time.Instant;

@Data
@Builder
public class TaxpayerDocumentResponseDto {
    private Long id;
    private String docType;
    private String fileUri;
    private String verificationStatus;
    private Instant uploadedDate;
}