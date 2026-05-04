package com.cognizant.paymentService.entity;


import com.cognizant.paymentService.entity.entityEnum.StatusBasic;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "revenue_record")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class RevenueRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long taxpayerId;

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusBasic status;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;
}