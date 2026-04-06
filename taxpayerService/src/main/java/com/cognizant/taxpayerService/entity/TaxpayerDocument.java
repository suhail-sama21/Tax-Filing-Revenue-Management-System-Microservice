package com.cognizant.taxpayerService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

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

    @Column(name = "doc_type", nullable = false, length = 50)
    private String docType; // e.g., "ID_PROOF", "ADDRESS_PROOF"

    @Column(name = "file_uri", nullable = false, columnDefinition = "text")
    private String fileUri;

    @Column(name = "verification_status", nullable = false, length = 30)
    private String verificationStatus = "Pending";

    @CreationTimestamp
    @Column(name = "uploaded_date", nullable = false, updatable = false)
    private Instant uploadedDate;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}