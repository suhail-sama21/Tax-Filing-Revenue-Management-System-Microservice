package com.cognizant.reportingService.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuditDto {
    private Long id;
    private Long officerId;
    private String scope;
    private String findings;
    private String status;
    private Instant createdAt;
}