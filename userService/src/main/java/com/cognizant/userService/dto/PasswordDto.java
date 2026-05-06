package com.cognizant.userService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
