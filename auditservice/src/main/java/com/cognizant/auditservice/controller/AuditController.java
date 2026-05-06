package com.cognizant.auditservice.controller;

import com.cognizant.auditservice.dto.AuditResponse;
import com.cognizant.auditservice.dto.CloseAuditRequest;
import com.cognizant.auditservice.dto.CreateAuditRequest;
import com.cognizant.auditservice.service.AuditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.cognizant.auditservice.dto.AuditDashboardResponse;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
//@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
@Slf4j
public class AuditController {

    private final AuditService auditService;

    @GetMapping("/dashboard")
    public ResponseEntity<AuditDashboardResponse> getDashboardSummary() {
        return ResponseEntity.ok(auditService.getDashboardSummary());
    }

    // --- Endpoint specifically hit by Compliance Service via Feign ---
    @PostMapping
    public ResponseEntity<AuditResponse> createAudit(@Valid @RequestBody CreateAuditRequest request) {
        log.info("START: Auto-creating Audit for Officer ID: {}", request.getOfficerId());
        AuditResponse response = auditService.createAudit(request);
        log.info("END: Audit created successfully with ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AuditResponse>> getAllAudits() {
        return ResponseEntity.ok(auditService.getAllAudits());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuditResponse> getAuditById(@PathVariable Long id) {
        return ResponseEntity.ok(auditService.getAuditById(id));
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<AuditResponse> closeAudit(
            @PathVariable Long id,
            @Valid @RequestBody CloseAuditRequest request) {
        log.info("START: Closing audit ID: {} | Findings: {}", id, request.getFindings());
        AuditResponse response = auditService.closeAudit(id, request);
        log.info("END: Audit closed successfully");
        return ResponseEntity.ok(response);
    }
}