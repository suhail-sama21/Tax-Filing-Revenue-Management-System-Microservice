package com.cognizant.Compliance_Service.entity;

import com.cognizant.Compliance_Service.entity.entityenum.ComplianceType;
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
@Builder
public class ComplianceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long taxpayerId;
    private Long filingId;
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    private ComplianceType type;

    private String result;

    private LocalDate date;

    @Column(columnDefinition = "text")
    private String notes;

    @CreationTimestamp
    private Instant createdAt;
}