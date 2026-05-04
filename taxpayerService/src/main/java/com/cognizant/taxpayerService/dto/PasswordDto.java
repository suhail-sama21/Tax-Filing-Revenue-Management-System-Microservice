package com.cognizant.taxpayerService.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PasswordDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    private String newPassword;
}
