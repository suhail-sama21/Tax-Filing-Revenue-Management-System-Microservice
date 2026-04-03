package com.cognizant.taxease.reportingService.entity;

import com.cognizant.taxease.reportingService.dto.responsedto.ComplianceResponse;
import com.cognizant.taxease.reportingService.entity.entityEnum.ComplianceType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "compliance_record")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ComplianceRecord extends ComplianceResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "compliance_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "taxpayer_id")
    private Taxpayer taxpayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "filing_id")
    private TaxFiling filing;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ComplianceType type; // Filing or Payment

    @Column(name = "result", nullable = false, length = 100)
    private String result;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    @PreUpdate
    private void validateTarget() {
        if (type == ComplianceType.Filing) {
            if (filing == null || payment != null) {
                throw new IllegalStateException("For Type=Filing, filing must be set and payment must be null.");
            }
        } else if (type == ComplianceType.Payment) {
            if (payment == null || filing != null) {
                throw new IllegalStateException("For Type=Payment, payment must be set and filing must be null.");
            }
        }
        if (date == null) date = LocalDate.now();
    }
}