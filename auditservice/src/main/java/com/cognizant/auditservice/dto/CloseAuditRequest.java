package com.cognizant.auditservice.dto;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CloseAuditRequest {

    @NotBlank(message = "Audit findings cannot be empty")
    @Size(max = 2000, message = "Findings cannot exeed 2000 characters")
    private String findings;
}
