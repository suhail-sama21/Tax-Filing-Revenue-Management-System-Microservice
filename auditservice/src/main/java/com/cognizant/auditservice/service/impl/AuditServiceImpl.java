package com.cognizant.auditservice.service.impl;

import com.cognizant.auditservice.dto.AuditResponse;
import com.cognizant.auditservice.dto.CloseAuditRequest;
import com.cognizant.auditservice.entity.Audit;
import com.cognizant.auditservice.entityenum.StatusBasic;
import com.cognizant.auditservice.repository.AuditRepository;
import com.cognizant.auditservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    @Override
    public List<AuditResponse> getAllAudits() {
        return auditRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public AuditResponse getAuditById(Long id) {
        Audit audit = auditRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Audit not found"));
        return mapToResponse(audit);
    }

    @Override
    public AuditResponse closeAudit(Long id, CloseAuditRequest request) {
        Audit audit = auditRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Audit not found"));

        if (request.getFindings() != null && !request.getFindings().isBlank()) {
            audit.setFindings(request.getFindings());
        }

        audit.setStatus(StatusBasic.Inactive);

        Audit saved = auditRepository.save(audit);
        return mapToResponse(saved);
    }

    private AuditResponse mapToResponse(Audit audit) {
        return AuditResponse.builder()
                .id(audit.getId())
                .officerId(audit.getOfficerId())
                .scope(audit.getScope())
                .findings(audit.getFindings())
                .status(audit.getStatus())
                .createdAt(audit.getCreatedAt())
                .build();
    }
}