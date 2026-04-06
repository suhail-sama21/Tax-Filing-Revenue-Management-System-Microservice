package com.cognizant.userService.dto;
import lombok.Data;

@Data
public class UpdateUserProfileRequest {
    private String address;
    private String contactInfo;
}