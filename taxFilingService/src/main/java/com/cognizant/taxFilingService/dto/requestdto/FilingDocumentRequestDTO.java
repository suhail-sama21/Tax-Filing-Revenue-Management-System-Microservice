package com.cognizant.taxFilingService.dto.requestdto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilingDocumentRequestDTO {

    @NotNull(message = "Filing ID is required to attach a document")
    private Long filingId;

    @NotBlank(message = "File URL or path cannot be empty")
    @Size(max = 1000, message = "File URL cannot exceed 1000 characters")
    private String fileUrl;
}