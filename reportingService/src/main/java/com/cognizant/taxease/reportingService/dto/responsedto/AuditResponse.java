package com.cognizant.taxease.reportingService.dto.responsedto;

import com.cognizant.taxease.reportingService.entity.entityEnum.StatusBasic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditResponse {

    private Long id;
    private Long officerId;
    private String scope;
    private String findings;
    private StatusBasic status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;
}