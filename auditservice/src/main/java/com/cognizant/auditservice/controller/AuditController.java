package com.cognizant.auditservice.controller;

import com.cognizant.auditservice.dto.AuditResponse;
import com.cognizant.auditservice.dto.CloseAuditRequest;
import com.cognizant.auditservice.service.AuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService auditService;

    @GetMapping
    public ResponseEntity<List<AuditResponse>> getAllAudits() {
        return ResponseEntity.ok(auditService.getAllAudits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditResponse> getAuditById(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.getAuditById(id));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<AuditResponse> closeAudit(@PathVariable Long id,
                                                    @Valid @RequestBody CloseAuditRequest request) {
        return ResponseEntity.ok(auditService.closeAudit(id, request));
    }
}