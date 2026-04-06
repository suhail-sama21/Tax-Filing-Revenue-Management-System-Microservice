package com.cognizant.auditservice.entity;

import com.cognizant.auditservice.entityenum.StatusBasic;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "audit_record")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Soft link to the User Service (The Officer)
    @Column(name = "officer_id", nullable = false)
    private Long officerId;

    @Column(nullable = false, length = 500)
    private String scope;

    @Column(columnDefinition = "text")
    private String findings;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusBasic status;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}