package com.cognizant.auditservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuditRequest {

    @NotBlank(message = "Audit findings cannot be empty")
    @Size(max = 2000, message = "Findings cannot exceed 2000 characters")
    private String findings;
}