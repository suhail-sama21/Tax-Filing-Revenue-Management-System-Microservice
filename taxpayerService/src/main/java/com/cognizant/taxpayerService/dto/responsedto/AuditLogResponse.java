package com.cognizant.taxpayerService.dto.responsedto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class AuditLogResponse {
    private Long id;
    private Long userId;
    private String action;
    private String resource;
    private Instant timestamp;
}