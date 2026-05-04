package com.cognizant.paymentService.entity;


import com.cognizant.paymentService.entity.entityEnum.PaymentMethod;
import com.cognizant.paymentService.entity.entityEnum.StatusBasic;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "payment")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long filingId;

    @Column(nullable = false)
    private Long taxpayerId; // Denormalized for fast lookups!

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod method;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusBasic status;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant date;
}