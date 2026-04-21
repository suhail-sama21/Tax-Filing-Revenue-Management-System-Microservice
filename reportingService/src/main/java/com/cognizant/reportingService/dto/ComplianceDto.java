package com.cognizant.reportingService.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComplianceDto {
    private Long id;
    private Long taxpayerId;
    private String type;
    private String result;
    private String notes;
    private LocalDate date;
}