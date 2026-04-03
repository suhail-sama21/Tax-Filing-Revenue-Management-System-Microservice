package com.cognizant.taxpayerService.controller;

import com.cognizant.taxpayerService.dto.requestdto.DocumentUpdateRequestDto;
import com.cognizant.taxpayerService.dto.requestdto.DocumentUploadRequestDto;
import com.cognizant.taxpayerService.dto.requestdto.UpdateTaxpayerProfileRequestDto;
import com.cognizant.taxpayerService.dto.responsedto.TaxpayerDocumentResponseDto;
import com.cognizant.taxpayerService.dto.responsedto.TaxpayerProfileResponseDto;
import com.cognizant.taxpayerService.service.TaxpayerProfileService;
import jakarta.validation.Valid; // Required for validation enforcement
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxpayers")
@RequiredArgsConstructor
@Slf4j
public class TaxpayerProfileController {

    private final TaxpayerProfileService taxpayerProfileService;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('TAXPAYER')")
    public ResponseEntity<TaxpayerProfileResponseDto> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("START: Fetching profile for user: {}", email);
        TaxpayerProfileResponseDto response = taxpayerProfileService.getProfile(email);
        log.info("END: Profile retrieved for user: {}", email);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/profile")
    @PreAuthorize("hasRole('TAXPAYER')")
    public ResponseEntity<TaxpayerProfileResponseDto> updateProfile(
            @Valid @RequestBody UpdateTaxpayerProfileRequestDto request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("START: Updating profile for user: {}", email);
        TaxpayerProfileResponseDto response = taxpayerProfileService.updateProfile(email, request);
        log.info("END: Profile update complete for: {}", email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/documents")
    @PreAuthorize("hasRole('TAXPAYER')")
    public ResponseEntity<List<TaxpayerDocumentResponseDto>> getDocuments() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("START: Fetching documents for user: {}", email);
        List<TaxpayerDocumentResponseDto> documents = taxpayerProfileService.getDocuments(email);
        log.info("END: Retrieved {} documents for user: {}", documents.size(), email);
        return ResponseEntity.ok(documents);
    }


    @PostMapping("/documents/upload")
    @PreAuthorize("hasRole('TAXPAYER')")
    public ResponseEntity<TaxpayerDocumentResponseDto> uploadDocument(
            @Valid @RequestBody DocumentUploadRequestDto request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("START: Document upload for user: {} | Type: {}", email, request.getDocType());
        TaxpayerDocumentResponseDto response = taxpayerProfileService.uploadDocument(email, request.getFileUri(), request.getDocType());
        log.info("END: Document uploaded successfully | ID: {}", response.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/documents/{documentId}")
    @PreAuthorize("hasRole('TAXPAYER')")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("START: Deleting document ID: {} for user: {}", documentId, email);
        taxpayerProfileService.deleteDocument(email, documentId);
        log.info("END: Document ID: {} deleted", documentId);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/documents/{documentId}")
    @PreAuthorize("hasRole('TAXPAYER')")
    public ResponseEntity<TaxpayerDocumentResponseDto> updateDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody DocumentUpdateRequestDto request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("START: Updating document ID: {} for user: {}", documentId, email);
        TaxpayerDocumentResponseDto updatedDocument = taxpayerProfileService.updateDocument(email, documentId, request.getFileUri());
        log.info("END: Document ID: {} updated", documentId);
        return ResponseEntity.ok(updatedDocument);
    }

    @PutMapping("/documents/{documentId}/verify")
    @PreAuthorize("hasRole('OFFICER') or hasRole('ADMINISTRATOR')")
    public ResponseEntity<TaxpayerDocumentResponseDto> verifyDocument(
            @PathVariable Long documentId,
            @Valid @RequestBody com.cognizant.taxpayerService.dto.requestdto.DocumentVerificationRequestDto request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("START: Verifying document ID: {} for officer: {}", documentId, email);
        TaxpayerDocumentResponseDto response = taxpayerProfileService.verifyDocumentStatus(email, documentId, request.getVerificationStatus());
        log.info("END: Document ID: {} verification status set to {}", documentId, request.getVerificationStatus());
        return ResponseEntity.ok(response);
    }
}