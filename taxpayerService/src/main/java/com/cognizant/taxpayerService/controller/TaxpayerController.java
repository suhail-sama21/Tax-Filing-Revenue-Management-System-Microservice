package com.cognizant.taxpayerService.controller;

import com.cognizant.taxpayerService.dto.*;
import com.cognizant.taxpayerService.entity.Taxpayer;
import com.cognizant.taxpayerService.service.TaxpayerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/taxpayers")
@RequiredArgsConstructor
@Slf4j
public class TaxpayerController {

    private final TaxpayerProfileService service;

    @PostMapping("/profile")
    @PreAuthorize("hasAnyRole('TAXPAYER','INTERNAL')")
    public ResponseEntity<Taxpayer> createProfile(@RequestParam Long userId, @RequestParam(required = false) String type) {
        log.info("Creating profile for userId: {}, type: {}", userId, type);
        return new ResponseEntity<>(service.createBaseProfile(userId, type), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}/full-profile")
    @PreAuthorize("hasAnyRole('TAXPAYER','INTERNAL')")
    public ResponseEntity<TaxpayerResponse> getProfile(@PathVariable Long userId) {
        // Verify the authenticated user is accessing their own profile
        log.info("Get profile for userId: {}", userId);
        return ResponseEntity.ok(service.getFullTaxpayerProfile(userId));
    }

    @PutMapping("/user/{userId}/profile")
    @PreAuthorize("hasAnyRole('TAXPAYER','INTERNAL')")
    public ResponseEntity<TaxpayerResponse> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateTaxpayerProfileRequestDto request) {
        // Verify the authenticated user is updating their own profile
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentUserId.equals(userId.toString())) {
            log.warn("Unauthorized profile update attempt: User {} tried to update profile for user {}", currentUserId, userId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only update your own profile");
        }

        return ResponseEntity.ok(service.updateProfile(userId, request));
    }

    @PostMapping("/user/{userId}/documents/upload")
    @PreAuthorize("hasAnyRole('TAXPAYER','INTERNAL')")
    public ResponseEntity<TaxpayerDocumentResponseDto> uploadDocument(
            @PathVariable Long userId, @Valid @RequestBody DocumentUploadRequestDto request) {
        // Verify the authenticated user is uploading documents for themselves
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentUserId.equals(userId.toString())) {
            log.warn("Unauthorized document upload attempt: User {} tried to upload for user {}", currentUserId, userId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only upload documents for yourself");
        }

        return new ResponseEntity<>(service.uploadDocument(userId, request), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}/documents")
    @PreAuthorize("hasAnyRole('TAXPAYER','INTERNAL')")
    public ResponseEntity<List<TaxpayerDocumentResponseDto>> getDocuments(@PathVariable Long userId) {
        // Verify the authenticated user is accessing their own documents
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentUserId.equals(userId.toString())) {
            log.warn("Unauthorized document access attempt: User {} tried to access documents for user {}", currentUserId, userId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only access your own documents");
        }

        return ResponseEntity.ok(service.getDocuments(userId));
    }

    @DeleteMapping("/user/{userId}/documents/{documentId}")
    @PreAuthorize("hasAnyRole('TAXPAYER','INTERNAL')")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long userId, @PathVariable Long documentId) {
        // Verify the authenticated user is deleting their own document
        String currentUserId = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!currentUserId.equals(userId.toString())) {
            log.warn("Unauthorized document deletion attempt: User {} tried to delete for user {}", currentUserId, userId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own documents");
        }

        service.deleteDocument(userId, documentId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/user/{userId}/documents/{documentId}/verify")
    @PreAuthorize("hasAnyRole('TAXPAYER','INTERNAL')")
    public ResponseEntity<TaxpayerDocumentResponseDto> updateDocumentStatus(
            @PathVariable Long userId,
            @PathVariable Long documentId,
            @Valid @RequestBody DocumentVerificationRequestDto request) {
        // Only COMPLIANCE role can verify documents
        log.info("Document verification by compliance officer for document {} of user {}", documentId, userId);
        return ResponseEntity.ok(service.updateDocumentStatus(userId, documentId, request.getStatus()));
    }
}