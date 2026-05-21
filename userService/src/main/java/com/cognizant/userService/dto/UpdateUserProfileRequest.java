package com.cognizant.userService.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateUserProfileRequest {
    private String name;
    private String phone;
    private String address;
    private String panNumber;
    private LocalDate dob;
}