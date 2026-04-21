package com.cognizant.taxpayerService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentVerificationRequestDto {
    @NotBlank(message = "status is required")
    private String status; // e.g., "Approved", "Rejected"
}