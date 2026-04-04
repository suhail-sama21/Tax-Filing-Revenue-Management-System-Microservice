package com.cognizant.taxpayerService.controller;

import com.cognizant.taxpayerService.dto.DocumentUpdateRequestDto;
import com.cognizant.taxpayerService.dto.DocumentUploadRequestDto;
import com.cognizant.taxpayerService.dto.DocumentVerificationRequestDto;
import com.cognizant.taxpayerService.dto.UpdateTaxpayerProfileRequestDto;
import com.cognizant.taxpayerService.dto.TaxpayerDocumentResponseDto;
import com.cognizant.taxpayerService.dto.TaxpayerProfileResponseDto;
import com.cognizant.taxpayerService.service.TaxpayerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxpayers")
@RequiredArgsConstructor
@Slf4j
public class TaxpayerProfileController {

    private final TaxpayerProfileService taxpayerProfileService;

    @GetMapping("/profile")
    public ResponseEntity<TaxpayerProfileResponseDto> getProfile(@RequestHeader("X-User-Email") String email) {
        log.info("START: Fetching profile for user: {}", email);
        TaxpayerProfileResponseDto response = taxpayerProfileService.getProfile(email);
        log.info("END: Profile retrieved for user: {}", email);
        return ResponseEntity.ok(response);
    }

    // ⚠️ TEMPORARY ENDPOINT TO TEST FEIGN CLIENT
    @GetMapping("/test-feign/{email}")
    public ResponseEntity<TaxpayerProfileResponseDto> testFeignIntegration(@PathVariable String email) {
        log.info("START: Testing Feign integration for email: {}", email);
        TaxpayerProfileResponseDto response = taxpayerProfileService.getProfile(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<TaxpayerProfileResponseDto> updateProfile(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody UpdateTaxpayerProfileRequestDto request) {
        log.info("START: Updating profile for user: {}", email);
        TaxpayerProfileResponseDto response = taxpayerProfileService.updateProfile(email, request);
        log.info("END: Profile update complete for: {}", email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/documents")
    public ResponseEntity<List<TaxpayerDocumentResponseDto>> getDocuments(@RequestHeader("X-User-Email") String email) {
        log.info("START: Fetching documents for user: {}", email);
        List<TaxpayerDocumentResponseDto> documents = taxpayerProfileService.getDocuments(email);
        log.info("END: Retrieved {} documents for user: {}", documents.size(), email);
        return ResponseEntity.ok(documents);
    }

    @PostMapping("/documents/upload")
    public ResponseEntity<TaxpayerDocumentResponseDto> uploadDocument(
            @RequestHeader("X-User-Email") String email,
            @Valid @RequestBody DocumentUploadRequestDto request) {
        log.info("START: Document upload for user: {} | Type: {}", email, request.getDocType());
        TaxpayerDocumentResponseDto response = taxpayerProfileService.uploadDocument(email, request.getFileUri(), request.getDocType());
        log.info("END: Document uploaded successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long documentId) {
        log.info("START: Deleting document ID: {} for user: {}", documentId, email);
        taxpayerProfileService.deleteDocument(email, documentId);
        log.info("END: Document ID: {} deleted", documentId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/documents/{documentId}")
    public ResponseEntity<TaxpayerDocumentResponseDto> updateDocument(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long documentId,
            @Valid @RequestBody DocumentUpdateRequestDto request) {
        log.info("START: Updating document ID: {} for user: {}", documentId, email);
        TaxpayerDocumentResponseDto updatedDocument = taxpayerProfileService.updateDocument(email, documentId, request.getFileUri());
        log.info("END: Document ID: {} updated", documentId);
        return ResponseEntity.ok(updatedDocument);
    }

    @PutMapping("/documents/{documentId}/verify")
    public ResponseEntity<TaxpayerDocumentResponseDto> verifyDocument(
            @RequestHeader("X-User-Email") String email,
            @PathVariable Long documentId,
            @Valid @RequestBody DocumentVerificationRequestDto request) {
        log.info("START: Verifying document ID: {} for officer/admin: {}", documentId, email);
        TaxpayerDocumentResponseDto response = taxpayerProfileService.verifyDocumentStatus(email, documentId, request.getVerificationStatus());
        log.info("END: Document ID: {} verification status set to {}", documentId, request.getVerificationStatus());
        return ResponseEntity.ok(response);
    }
}