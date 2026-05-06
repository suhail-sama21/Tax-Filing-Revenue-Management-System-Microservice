package com.cognizant.auditservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAuditRequest {

    @NotNull(message = "A target taxpayer must be specified")
    private Long taxpayerId; // <-- ADD THIS

    @NotNull
    private Long officerId;

    @NotBlank
    private String scope;

    private String findings;
}