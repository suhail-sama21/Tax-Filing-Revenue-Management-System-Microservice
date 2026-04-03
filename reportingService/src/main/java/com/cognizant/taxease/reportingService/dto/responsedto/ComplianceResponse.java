package com.cognizant.taxease.reportingService.dto.responsedto;

import com.cognizant.taxease.reportingService.entity.entityEnum.ComplianceType;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceResponse {

    private Long id;
    private Long taxpayerId;
    private Long filingId; // Optional, might be null if compliance isn't tied to a specific filing
    private Long paymentId; // Optional

    private ComplianceType type;
    private String result;

    // Formats to "2026-03-22"
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private String notes;

    // Formats to standard UTC ISO-8601
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;
}