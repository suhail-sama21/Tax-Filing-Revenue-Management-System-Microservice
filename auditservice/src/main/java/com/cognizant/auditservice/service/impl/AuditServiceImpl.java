package com.cognizant.auditservice.service.impl;

import com.cognizant.auditservice.dto.AuditResponse;
import com.cognizant.auditservice.dto.CloseAuditRequest;
import com.cognizant.auditservice.dto.CreateAuditRequest;
import com.cognizant.auditservice.entity.Audit;
import com.cognizant.auditservice.entityenum.StatusBasic;
import com.cognizant.auditservice.repository.AuditRepository;
import com.cognizant.auditservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    @Override
    @Transactional
    public AuditResponse createAudit(CreateAuditRequest request) {
        log.info("Creating new Audit for Officer ID: {}", request.getOfficerId());

        Audit audit = Audit.builder()
                .officerId(request.getOfficerId())
                .scope(request.getScope())
                .findings(request.getFindings() != null ? request.getFindings() : "Auto-detected issue")
                .status(StatusBasic.Active)
                .build();

        Audit savedAudit = auditRepository.save(audit);
        return mapToResponse(savedAudit);
    }

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
    @Transactional
    public AuditResponse closeAudit(Long id, CloseAuditRequest request) {
        log.info("Closing audit ID: {}", id);
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