package com.cognizant.auditLogService.repository;

import com.cognizant.auditLogService.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}