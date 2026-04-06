package com.cognizant.taxpayerService.dto;

import lombok.Data;

@Data
public class DocumentUploadRequestDto {
    private String fileUri;
    private String docType;
}