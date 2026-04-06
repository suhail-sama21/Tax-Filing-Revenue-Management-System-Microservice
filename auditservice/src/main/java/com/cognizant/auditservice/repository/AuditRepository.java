package com.cognizant.auditservice.repository;

import com.cognizant.auditservice.entity.Audit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<Audit, Long> {
}