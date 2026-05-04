package com.cognizant.taxFilingService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tax_filing", indexes = { @Index(name = "idx_filing_taxpayer", columnList = "taxpayer_id") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaxFiling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filing_id")
    private Long id;

    // Soft link to the Taxpayer/User Service
    @Column(name = "taxpayer_id", nullable = false)
    private Long taxpayerId;

    @Column(name = "period", nullable = false, length = 20)
    private String period; // e.g., FY2025-26

    @Column(name = "amount_declared", nullable = false, precision = 14, scale = 2)
    private BigDecimal amountDeclared;

    @Column(name = "status", nullable = false, length = 30)
    private String status = "Pending";

    // Soft link to User Service for the Officer who approved/rejected it
    @Column(name = "officer_id")
    private Long officerId;

    @CreationTimestamp
    @Column(name = "submitted_date", nullable = false, updatable = false)
    private Instant submittedDate;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    // A Filing can still have a hard relationship with its own Documents
    @OneToMany(mappedBy = "filing", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FilingDocument> filingDocuments = new ArrayList<>();
}