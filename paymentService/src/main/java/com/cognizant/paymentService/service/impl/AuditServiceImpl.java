package com.cognizant.paymentService.service.impl;

import com.cognizant.taxease.dao.AuditRepository;
import com.cognizant.taxease.dto.responsedto.AuditResponse;
import com.cognizant.taxease.dto.requestdto.CloseAuditRequest;
import com.cognizant.taxease.entity.Audit;
import com.cognizant.taxease.entity.entityEnum.StatusBasic;
import com.cognizant.taxease.service.AuditLogService;
import com.cognizant.taxease.service.AuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;
    private final AuditLogService auditLogService;

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
        auditLogService.record("AUDIT_VIEW", "audits/" + id);
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

        Audit savedAudit = auditRepository.save(audit);
        auditLogService.record("AUDIT_CLOSE", "audits/" + savedAudit.getId());
        return mapToResponse(savedAudit);
    }

    private AuditResponse mapToResponse(Audit audit) {
        return new AuditResponse(
                audit.getId(),
                audit.getOfficer() != null ? audit.getOfficer().getId() : null,
                audit.getScope(),
                audit.getFindings(),
                audit.getStatus(),
                audit.getCreatedAt()

        );
    }
}
