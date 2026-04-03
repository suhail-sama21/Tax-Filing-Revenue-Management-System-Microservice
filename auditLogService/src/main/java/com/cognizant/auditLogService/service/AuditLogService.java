package com.cognizant.auditLogService.service;

import com.cognizant.auditLogService.entity.AuditLog;

import java.util.List;

public interface AuditLogService {
    public void record(String action, String resource);
    public void recordRegistration(User newUser, String action, String resource);
    public List<AuditLog> list();
    public AuditLog get(Long id);
}

