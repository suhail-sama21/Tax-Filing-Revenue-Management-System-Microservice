package com.cognizant.taxpayerService.entity;

import com.cognizant.taxpayerService.entity.entityEnum.StatusBasic;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "revenue_record",
        indexes = { @Index(name = "idx_revenue_taxpayer_date", columnList = "taxpayer_id, date") })
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RevenueRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "revenue_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "taxpayer_id", nullable = false)
    private Taxpayer taxpayer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Column(name = "amount", nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @CreationTimestamp
    @Column(name = "date", nullable = false, updatable = false)
    private Instant date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusBasic status = StatusBasic.Completed;
}