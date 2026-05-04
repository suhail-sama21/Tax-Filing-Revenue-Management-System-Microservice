package com.cognizant.taxpayerService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentUploadRequestDto {
    @NotBlank(message = "fileUri is required")
    private String fileUri;

    @NotBlank(message = "docType is required")
    private String docType;
}