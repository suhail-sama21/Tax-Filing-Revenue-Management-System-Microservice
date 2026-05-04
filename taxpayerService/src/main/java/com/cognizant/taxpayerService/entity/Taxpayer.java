package com.cognizant.taxpayerService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "taxpayer")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Taxpayer {

    // Taxpayer ID and User ID are exactly the same!
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "taxpayer_id_number", nullable = false, unique = true, length = 11)
    private String taxpayerIdNumber; // The 11-digit business/citizen ID

    @Column(name = "type", nullable = false, length = 30)
    private String type; // Citizen or Business

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}