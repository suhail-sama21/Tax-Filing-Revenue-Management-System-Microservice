package com.cognizant.taxpayerService.controller;

import com.cognizant.taxpayerService.dto.*;
import com.cognizant.taxpayerService.entity.Taxpayer;
import com.cognizant.taxpayerService.service.TaxpayerProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taxpayers")
@RequiredArgsConstructor
public class TaxpayerController {

    private final TaxpayerProfileService service;

    @PostMapping("/profile")
    public ResponseEntity<Taxpayer> createProfile(@RequestParam Long userId, @RequestParam(required = false) String type) {
        return new ResponseEntity<>(service.createBaseProfile(userId, type), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}/full-profile")
    public ResponseEntity<TaxpayerResponse> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getFullTaxpayerProfile(userId));
    }

    @PutMapping("/user/{userId}/profile")
    public ResponseEntity<TaxpayerResponse> updateProfile(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateTaxpayerProfileRequestDto request) {
        return ResponseEntity.ok(service.updateProfile(userId, request));
    }

    @PostMapping("/user/{userId}/documents/upload")
    public ResponseEntity<TaxpayerDocumentResponseDto> uploadDocument(
            @PathVariable Long userId, @Valid @RequestBody DocumentUploadRequestDto request) {
        return new ResponseEntity<>(service.uploadDocument(userId, request), HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}/documents")
    public ResponseEntity<List<TaxpayerDocumentResponseDto>> getDocuments(@PathVariable Long userId) {
        return ResponseEntity.ok(service.getDocuments(userId));
    }

    @DeleteMapping("/user/{userId}/documents/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long userId, @PathVariable Long documentId) {
        service.deleteDocument(userId, documentId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/user/{userId}/documents/{documentId}/verify")
    public ResponseEntity<TaxpayerDocumentResponseDto> updateDocumentStatus(
            @PathVariable Long userId,
            @PathVariable Long documentId,
            @RequestBody DocumentVerificationRequestDto request) {

        return ResponseEntity.ok(service.updateDocumentStatus(userId, documentId, request.getStatus()));
    }
}