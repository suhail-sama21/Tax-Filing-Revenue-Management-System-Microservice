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
    @NotNull
    private Long officerId;

    @NotBlank
    private String scope;

    private String findings;
}