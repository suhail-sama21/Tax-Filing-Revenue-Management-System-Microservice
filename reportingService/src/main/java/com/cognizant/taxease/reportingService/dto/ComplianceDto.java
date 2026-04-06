package com.cognizant.taxease.reportingService.dto;
import lombok.Data;
import java.time.LocalDate;

@Data
public class ComplianceDto {
    private Long id;
    private Long taxpayerId;
    private String type;
    private String result;
    private String notes;
    private LocalDate date;
}