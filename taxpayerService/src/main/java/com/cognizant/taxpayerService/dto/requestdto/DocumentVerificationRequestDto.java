package com.cognizant.taxpayerService.dto.requestdto;

import com.cognizant.taxpayerService.entity.entityEnum.VerificationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentVerificationRequestDto {

    @NotNull(message = "Verification status is required")
    private VerificationStatus verificationStatus;
}
