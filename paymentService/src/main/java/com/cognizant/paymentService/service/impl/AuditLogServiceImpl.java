package com.cognizant.paymentService.service.impl;

import com.cognizant.taxease.dao.AuditLogRepository;
import com.cognizant.taxease.dao.UserRepository;
import com.cognizant.taxease.entity.AuditLog;
import com.cognizant.taxease.entity.User;
import com.cognizant.taxease.service.AuditLogService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@Data
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    // Standard record method for logged-in users
    @Override
    public void record(String action, String resource) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;

        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            user = userRepository.findByEmail(auth.getName()).orElse(null);
        }

        saveLog(user, action, resource);
    }

    // Specialized record method for Registration (Pass user directly)
    public void recordRegistration(User newUser, String action, String resource) {
        saveLog(newUser, action, resource);
    }

    @Override
    public List<AuditLog> list() {
        return auditLogRepository.findAll();
    }

    @Override
    public AuditLog get(Long id) {
        return auditLogRepository.findById(id).orElseThrow(()->new NoSuchElementException("No record found"));
    }

    private void saveLog(User user, String action, String resource) {
        AuditLog log = AuditLog.builder()
                .user(user) // Can be null or the specific new user
                .action(action)
                .resource(resource)
                .build();
        auditLogRepository.save(log);
    }
}