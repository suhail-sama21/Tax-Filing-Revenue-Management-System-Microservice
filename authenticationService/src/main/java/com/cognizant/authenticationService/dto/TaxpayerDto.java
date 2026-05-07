package com.cognizant.authenticationService.dto;

import lombok.*;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaxpayerDto {
    private Long userId;
    private String taxpayerIdNumber; // The 11-digit business/citizen ID
    private String type; // Citizen or Business
    private Instant createdAt;
}