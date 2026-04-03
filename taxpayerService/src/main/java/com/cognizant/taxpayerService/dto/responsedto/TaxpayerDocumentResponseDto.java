package com.cognizant.taxpayerService.dto.responsedto;

import com.cognizant.taxpayerService.entity.entityEnum.DocTypeTaxpayer;
import com.cognizant.taxpayerService.entity.entityEnum.VerificationStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class TaxpayerDocumentResponseDto {
    private Long id;
    private DocTypeTaxpayer docType;
    private String fileUri;
    private VerificationStatus verificationStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant uploadedDate;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant updatedAt;
}