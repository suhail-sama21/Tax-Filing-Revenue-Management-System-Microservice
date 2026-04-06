package com.cognizant.taxpayerService.dto;

import lombok.Data;

@Data
public class DocumentVerificationRequestDto {
    private String status; // e.g., "Approved", "Rejected"
}