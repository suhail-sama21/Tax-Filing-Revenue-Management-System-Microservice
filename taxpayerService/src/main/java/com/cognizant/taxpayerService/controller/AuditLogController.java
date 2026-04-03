package com.cognizant.taxpayerService.controller;

import com.cognizant.taxpayerService.dto.responsedto.AuditLogResponse;
import com.cognizant.taxpayerService.entity.AuditLog;
import com.cognizant.taxpayerService.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;


    @GetMapping
    public List<AuditLogResponse> list() {
        log.info("START: Listing all audit logs");
        List<AuditLogResponse> response = auditLogService.list().stream().map(AuditLogController::toResponse).toList();
        log.info("END: Retrieved {} log entries", response.size());
        return response;
    }

    @GetMapping("/{id}")
    public AuditLogResponse get(@PathVariable Long id) {
        log.info("START: Fetching audit log ID: {}", id);
        AuditLogResponse response = toResponse(auditLogService.get(id));
        log.info("END: Retrieved log entry for ID: {}", id);
        return response;
    }


    private static AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(),
                log.getUser() != null ? log.getUser().getId() : null,
                log.getAction(),
                log.getResource(),
                log.getTimestamp()
        );
    }
}
