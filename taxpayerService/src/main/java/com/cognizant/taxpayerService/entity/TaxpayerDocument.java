package com.cognizant.taxpayerService.entity;


import com.cognizant.taxpayerService.entity.entityEnum.DocTypeTaxpayer;
import com.cognizant.taxpayerService.entity.entityEnum.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.*;


@Entity
@Table(name = "taxpayer_document")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TaxpayerDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "taxpayer_id", nullable = false)
    private Taxpayer taxpayer;

    @Enumerated(EnumType.STRING)
    @Column(name = "doc_type", nullable = false, length = 50)
    private DocTypeTaxpayer docType;

    @Column(name = "file_uri", nullable = false, columnDefinition = "text")
    private String fileUri;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status", nullable = false, length = 30)
    private VerificationStatus verificationStatus = VerificationStatus.Pending;

    @CreationTimestamp
    @Column(name = "uploaded_date", nullable = false, updatable = false)
    private Instant uploadedDate;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}