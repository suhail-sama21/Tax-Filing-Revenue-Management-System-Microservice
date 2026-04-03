package com.cognizant.taxFilingService.entity;

import com.cognizant.taxFilingService.entity.entityEnum.TaxpayerType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "taxpayer")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Taxpayer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taxpayer_id")
    private Long id;

    // Owning side of 1:1 with User
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Column(name = "taxpayer_id_number", nullable = false, unique = true, length = 11)
    private String taxpayerIdNumber; // 11-digit unique ID

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 30)
    private TaxpayerType type; // Citizen/Business

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

    @OneToMany(mappedBy = "taxpayer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TaxFiling> taxFilings = new ArrayList<>();

    @OneToMany(mappedBy = "taxpayer", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TaxpayerDocument> taxpayerDocuments = new HashSet<>();

    @OneToMany(mappedBy = "taxpayer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComplianceRecord> complianceRecords = new ArrayList<>();

    @OneToMany(mappedBy = "taxpayer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RevenueRecord> revenueRecords = new ArrayList<>();
}