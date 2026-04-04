package com.cognizant.taxpayerService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaxpayerProfileRequestDto {
    // testing some structure
    @NotBlank(message = "Address cannot be empty")
    @Size(max = 500, message = "Address must not exceed 500 characters")
    private String address;

    @NotBlank(message = "Contact info cannot be empty")
    @Size(max = 200, message = "Contact info must not exceed 200 characters")
    private String contactInfo;
}
