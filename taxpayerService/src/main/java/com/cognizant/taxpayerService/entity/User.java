package com.cognizant.taxpayerService.entity;

import com.cognizant.taxpayerService.entity.entityEnum.UserRole;
import com.cognizant.taxpayerService.entity.entityEnum.StatusBasic; // or a dedicated UserStatus enum
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "`user`",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email", unique = true),
                @Index(name = "idx_user_role_status", columnList = "role,status")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 40)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusBasic status = StatusBasic.Active;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "password_changed_at")
    private Instant passwordChangedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // Inverse side of 1:1 with Taxpayer
    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private Taxpayer taxpayer;
}