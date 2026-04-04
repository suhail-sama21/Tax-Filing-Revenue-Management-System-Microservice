package com.cognizant.taxpayerService.entity;

import com.cognizant.taxpayerService.entity.entityEnum.TaxpayerType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "taxpayer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Taxpayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taxpayer_id")
    private Long id;

    // MICROSERVICE CHANGE: Replaced the User object with just the email string
    @Column(name = "user_email", unique = true, nullable = false, length = 255)
    private String userEmail;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "taxpayer_id_number", nullable = false, unique = true, length = 11)
    private String taxpayerIdNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private TaxpayerType type;

    @Column(name = "address", columnDefinition = "text")
    private String address;

    @Column(name = "contact_info", columnDefinition = "text")
    private String contactInfo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // ... Keep your OneToMany mappings for Filings, Documents, etc. ...
}