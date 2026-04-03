package com.cognizant.taxpayerService.service;

import com.cognizant.taxpayerService.dto.responsedto.TaxpayerDocumentResponseDto;
import com.cognizant.taxpayerService.dto.responsedto.TaxpayerProfileResponseDto;
import com.cognizant.taxpayerService.dto.requestdto.UpdateTaxpayerProfileRequestDto;
import com.cognizant.taxpayerService.entity.entityEnum.DocTypeTaxpayer;

import java.util.List;

public interface TaxpayerProfileService {
    TaxpayerProfileResponseDto getProfile(String email);
    TaxpayerProfileResponseDto updateProfile(String email, UpdateTaxpayerProfileRequestDto request);
    List<TaxpayerDocumentResponseDto> getDocuments(String email);
    TaxpayerDocumentResponseDto uploadDocument(String email, String fileUri, DocTypeTaxpayer docType);
    void deleteDocument(String email, Long documentId);
    TaxpayerDocumentResponseDto updateDocument(String email, Long documentId, String fileUri);
    TaxpayerDocumentResponseDto verifyDocumentStatus(String email, Long documentId, com.cognizant.taxpayerService.entity.entityEnum.VerificationStatus verificationStatus);
}