package com.cognizant.taxease.reportingService.dto;
import lombok.Data;
import java.time.Instant;

@Data
public class AuditDto {
    private Long id;
    private Long officerId;
    private String scope;
    private String findings;
    private String status; // Active or Inactive
    private Instant createdAt;
}