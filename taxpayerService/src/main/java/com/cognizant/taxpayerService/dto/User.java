package com.cognizant.taxpayerService.dto;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {


    private Long id;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String role;
    private String address;
    private String panNumber;
    private LocalDate dob;
    private Instant createdAt;
    private Instant updatedAt;
}