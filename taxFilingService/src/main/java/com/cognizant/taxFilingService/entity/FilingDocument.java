package com.cognizant.taxFilingService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "filing_document")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FilingDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;

    // Hard relationship because they live in the same DB
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "filing_id", nullable = false)
    private TaxFiling filing;

    @Column(name = "file_url", nullable = false, columnDefinition = "text")
    private String fileUrl;

    @CreationTimestamp
    @Column(name = "uploaded_date", updatable = false, nullable = false)
    private Instant uploadedDate;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}