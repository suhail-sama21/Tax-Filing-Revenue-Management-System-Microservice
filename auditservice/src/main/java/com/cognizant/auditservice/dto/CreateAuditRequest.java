package com.cognizant.auditservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAuditRequest {
    @NotNull
    private Long officerId;

    @NotBlank
    private String scope;

    private String findings;
}