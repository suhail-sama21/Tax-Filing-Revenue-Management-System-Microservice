package com.cognizant.taxpayerService.dao;

import com.cognizant.taxpayerService.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}