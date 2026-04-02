package com.cognizant.Compliance_Service.dto;

import com.cognizant.Compliance_Service.entity.entityenum.ComplianceType;
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
public class CreateComplianceRequest {

    @NotNull(message = "Taxpayer ID is required to create a compliance record")
    private Long taxpayerId;

    // I left these without @NotNull because a compliance check might be on the
    // whole user profile, not necessarily tied to a specific filing or payment.
    // If your business logic requires them, feel free to add @NotNull!
    private Long filingId;
    private Long paymentId;

    @NotNull(message = "Compliance type (e.g., AUDIT, REVIEW) is required")
    private ComplianceType type; // payment or filing

    @NotBlank(message = "Compliance result is required")
    @Size(max = 50, message = "Result status cannot exceed 50 characters")
    private String result; //compliance or non-compliance

    @Size(max = 1000, message = "Notes cannot exceed 1000 characters")
    private String notes;
}