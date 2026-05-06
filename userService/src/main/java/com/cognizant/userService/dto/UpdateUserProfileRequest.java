package com.cognizant.userService.dto;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserProfileRequest {
    private String address;
    private String panNumber;
    private LocalDate dob;
}