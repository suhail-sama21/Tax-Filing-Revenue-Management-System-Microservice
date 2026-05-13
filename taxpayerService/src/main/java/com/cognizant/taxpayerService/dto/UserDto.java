package com.cognizant.taxpayerService.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String role;

    private String address;
    // 1. Rename to match the entity and other DTOs
    private String panNumber;
    // 2. Add the new date of birth field
    private LocalDate dob;
}