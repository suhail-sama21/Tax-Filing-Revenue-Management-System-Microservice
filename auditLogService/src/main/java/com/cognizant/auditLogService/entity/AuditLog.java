package com.cognizant.auditLogService.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Immutable;

import java.time.Instant;

@Entity
@Table(name = "audit_log",
        indexes = {
                @Index(name = "idx_auditlog_user", columnList = "user_id"),
                @Index(name = "idx_auditlog_ts", columnList = "timestamp"),
                @Index(name = "idx_auditlog_action_ts", columnList = "action,timestamp")
        })
@Immutable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private Long user;

    @Column(name = "action", nullable = false, length = 100)
    private String action;   // LOGIN, UPDATE_PROFILE, APPROVE_FILING, etc.

    @Column(name = "resource", nullable = false, length = 200)
    private String resource; //  /api/filings/123

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private Instant timestamp;
}


