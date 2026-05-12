package com.cognizant.taxpayerService.service;

import com.cognizant.taxpayerService.dto.*;
import com.cognizant.taxpayerService.entity.Taxpayer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TaxpayerProfileService {

    void createBaseProfile(String email, String type);

    TaxpayerResponse getFullTaxpayerProfile(Long userId);

    TaxpayerResponse updateProfile(Long userId, UpdateTaxpayerProfileRequestDto request);

    List<TaxpayerDocumentResponseDto> getDocuments(Long userId);

    TaxpayerDocumentResponseDto uploadDocument(Long userId, DocumentUploadRequestDto request);

    TaxpayerDocumentResponseDto updateDocument(Long userId, Long documentId, DocumentUploadRequestDto request);

    void deleteDocument(Long userId, Long documentId);

    TaxpayerDocumentResponseDto updateDocumentStatus(Long userId, Long documentId, String newStatus);

    java.util.List<com.cognizant.taxpayerService.dto.TaxpayerPendingDocumentDto> getTaxpayersWithPendingDocuments();

    String getTaxPayerType(Long id);

    String getMailForUserID(Long userId);

    ResponseEntity<String> changePassword(Long userId, PasswordDto passwordDto);
}
