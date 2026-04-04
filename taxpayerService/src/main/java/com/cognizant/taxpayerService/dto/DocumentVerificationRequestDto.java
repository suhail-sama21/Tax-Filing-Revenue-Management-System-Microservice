package com.cognizant.taxpayerService.dto;

import com.cognizant.taxpayerService.entity.entityEnum.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentVerificationRequestDto {

    @NotNull(message = "Verification status is required")
    private VerificationStatus verificationStatus;

}