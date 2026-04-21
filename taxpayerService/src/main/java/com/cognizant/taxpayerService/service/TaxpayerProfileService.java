package com.cognizant.taxpayerService.service;

import com.cognizant.taxpayerService.dto.DocumentUploadRequestDto;
import com.cognizant.taxpayerService.dto.TaxpayerDocumentResponseDto;
import com.cognizant.taxpayerService.dto.TaxpayerResponse;
import com.cognizant.taxpayerService.dto.UpdateTaxpayerProfileRequestDto;
import com.cognizant.taxpayerService.entity.Taxpayer;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TaxpayerProfileService {

    Taxpayer createBaseProfile(Long userId, String type);

    TaxpayerResponse getFullTaxpayerProfile(Long userId);

    TaxpayerResponse updateProfile(Long userId, UpdateTaxpayerProfileRequestDto request);

    List<TaxpayerDocumentResponseDto> getDocuments(Long userId);

    TaxpayerDocumentResponseDto uploadDocument(Long userId, DocumentUploadRequestDto request);

    void deleteDocument(Long userId, Long documentId);

    TaxpayerDocumentResponseDto updateDocumentStatus(Long userId, Long documentId, String newStatus);

    String getTaxPayerType(Long id);

    String getMailForUserID(Long userId);
}
