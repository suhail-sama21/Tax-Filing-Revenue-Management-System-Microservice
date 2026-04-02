package com.cognizant.auditservice.service;

import com.cognizant.auditservice.dto.CloseAuditRequest;
import com.cognizant.auditservice.dto.AuditResponse;

import java.util.List;

public interface AuditService {

    List<AuditResponse> getAllAudits();

    AuditResponse getAuditById(Long id);

    AuditResponse closeAudit(Long id, CloseAuditRequest request);
}
