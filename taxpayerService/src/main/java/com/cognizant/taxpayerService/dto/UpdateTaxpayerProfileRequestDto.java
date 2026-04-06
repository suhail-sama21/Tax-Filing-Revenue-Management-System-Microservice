package com.cognizant.taxpayerService.dto;

import lombok.Data;

@Data
public class UpdateTaxpayerProfileRequestDto {
    private String address;
    private String contactInfo;
}