package com.cognizant.auditservice.entity;

import com.cognizant.auditservice.entityenum.StatusBasic;
import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "audit_case",
        indexes = { @Index(name = "idx_audit_officer", columnList = "officer_id") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Audit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private Long id;


//    @ManyToOne(fetch = FetchType.LAZY, optional = false)
//    @JoinColumn(name = "officer_id", nullable = false)
    @Column(name = "officer_id")
    private Long officerId;

    @Column(name = "scope", length = 200)
    private String scope;

    @Lob
    @Column(name = "findings")
    private String findings;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private StatusBasic status;
}