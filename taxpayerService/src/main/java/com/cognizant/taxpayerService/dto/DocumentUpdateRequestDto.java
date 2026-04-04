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
public class DocumentUpdateRequestDto {

    @NotBlank(message = "File URI or path cannot be empty")
    @Size(max = 1000, message = "File URI cannot exceed 1000 characters")
    private String fileUri;
}
