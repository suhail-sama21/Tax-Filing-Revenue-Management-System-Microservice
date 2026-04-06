package com.cognizant.userService.dto;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;
    private String address;      // Added
    private String contactInfo;  // Added
}