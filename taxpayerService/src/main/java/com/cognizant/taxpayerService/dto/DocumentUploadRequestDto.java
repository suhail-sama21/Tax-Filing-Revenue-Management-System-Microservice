package com.cognizant.taxpayerService.dto;

import com.cognizant.taxpayerService.entity.entityEnum.DocTypeTaxpayer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadRequestDto {

    @NotBlank(message = "File URI or path cannot be empty")
    @Size(max = 1000, message = "File URI cannot exceed 1000 characters")
    private String fileUri;

    @NotNull(message = "Document type (e.g., ID_PROOF, ADDRESS_PROOF) is required")
    private DocTypeTaxpayer docType;
}