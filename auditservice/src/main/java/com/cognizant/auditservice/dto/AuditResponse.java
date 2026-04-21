package com.cognizant.auditservice.dto;

import com.cognizant.auditservice.entityenum.StatusBasic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuditResponse {

    private Long id;
    private Long officerId;
    private String scope;
    private String findings;
    private StatusBasic status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;
}