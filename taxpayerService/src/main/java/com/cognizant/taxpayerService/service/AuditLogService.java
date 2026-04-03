package com.cognizant.taxpayerService.service;

import com.cognizant.taxpayerService.entity.AuditLog;
import com.cognizant.taxpayerService.entity.User;

import java.util.List;

public interface AuditLogService {
    public void record(String action, String resource);
    public void recordRegistration(User newUser, String action, String resource);
    public List<AuditLog> list();
    public AuditLog get(Long id);
}
