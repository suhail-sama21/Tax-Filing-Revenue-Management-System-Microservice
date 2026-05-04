package com.cognizant.taxpayerService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateTaxpayerProfileRequestDto {
    @NotBlank(message = "address is required")
    private String address;

    @NotBlank(message = "contactInfo is required")
    private String contactInfo;
}