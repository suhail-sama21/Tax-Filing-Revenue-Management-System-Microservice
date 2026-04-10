package com.cognizant.taxpayerService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentUploadRequestDto {
    @NotBlank(message = "fileUri is required")
    private String fileUri;

    @NotBlank(message = "docType is required")
    private String docType;
}