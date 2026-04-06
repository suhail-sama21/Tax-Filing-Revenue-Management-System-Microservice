package com.cognizant.taxFilingService.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FilingDocumentRequestDTO {
    @NotNull(message = "Filing ID is required")
    private Long filingId;

    @NotBlank(message = "File URL cannot be empty")
    private String fileUrl;
}