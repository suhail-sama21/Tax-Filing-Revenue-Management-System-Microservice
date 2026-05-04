package com.cognizant.taxFilingService.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FilingDocumentRequestDTO {
    @NotNull(message = "Filing ID is required")
    private Long filingId;

    @NotBlank(message = "File URL cannot be empty")
    @Size(max = 1000, message = "URL is too long")
    private String fileUrl;
}