package com.cognizant.taxFilingService.entity;

import com.cognizant.taxFilingService.entity.entityEnum.StatusBasic;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tax_filing",
        indexes = { @Index(name = "idx_filing_taxpayer", columnList = "taxpayer_id") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaxFiling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filing_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "taxpayer_id", nullable = false)
    private Taxpayer taxpayer;

    @Column(name = "period", nullable = false, length = 20)
    private String period; // FY2025-26 or 2026Q1

    @Column(name = "amount_declared", nullable = false, precision = 14, scale = 2)
    private BigDecimal amountDeclared;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusBasic status = StatusBasic.Pending;

    // Officer (User with role OFFICER) who touched this filing
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "officer_id")
    private User officer;

    @CreationTimestamp
    @Column(name = "submitted_date", nullable = false, updatable = false)
    private Instant submittedDate;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

//    @OneToMany(mappedBy = "filing", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Payment> payments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "filing", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<FilingDocument> filingDocuments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "filing", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ComplianceRecord> complianceRecords = new ArrayList<>();
}