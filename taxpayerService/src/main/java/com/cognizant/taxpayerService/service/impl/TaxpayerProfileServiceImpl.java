package com.cognizant.taxpayerService.service.impl;

import com.cognizant.taxpayerService.client.AuditServiceClient;
import com.cognizant.taxpayerService.client.UserServiceClient;
import com.cognizant.taxpayerService.dao.TaxpayerDocumentRepository;
import com.cognizant.taxpayerService.dao.TaxpayerRepository;
import com.cognizant.taxpayerService.dto.UserDTO;
import com.cognizant.taxpayerService.dto.UpdateTaxpayerProfileRequestDto;
import com.cognizant.taxpayerService.dto.TaxpayerDocumentResponseDto;
import com.cognizant.taxpayerService.dto.TaxpayerProfileResponseDto;
import com.cognizant.taxpayerService.entity.Taxpayer;
import com.cognizant.taxpayerService.entity.TaxpayerDocument;
import com.cognizant.taxpayerService.entity.entityEnum.DocTypeTaxpayer;
import com.cognizant.taxpayerService.entity.entityEnum.VerificationStatus;
import com.cognizant.taxpayerService.service.TaxpayerProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxpayerProfileServiceImpl implements TaxpayerProfileService {

    // Feign Clients for external microservices
    private final UserServiceClient userServiceClient;
    private final AuditServiceClient auditServiceClient;

    // Local Repositories
    private final TaxpayerRepository taxpayerRepository;
    private final TaxpayerDocumentRepository taxpayerDocumentRepository;

    @Override
    public TaxpayerProfileResponseDto getProfile(String email) {
        Taxpayer taxpayer = getTaxpayerByEmail(email);

        // Fetch User data over HTTP via Feign
        UserDTO user = userServiceClient.getUserByEmail(email);

        return TaxpayerProfileResponseDto.builder()
                .taxpayerId(taxpayer.getId())
                .taxpayerIdNumber(taxpayer.getTaxpayerIdNumber())
                .name(taxpayer.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .address(taxpayer.getAddress())
                .contactInfo(taxpayer.getContactInfo())
                .type(taxpayer.getType())
                .build();
    }

    @Override
    @Transactional
    public TaxpayerProfileResponseDto updateProfile(String email, UpdateTaxpayerProfileRequestDto request) {
        Taxpayer taxpayer = getTaxpayerByEmail(email);

        taxpayer.setAddress(request.getAddress());
        taxpayer.setContactInfo(request.getContactInfo());
        taxpayerRepository.save(taxpayer);

        // Microservice Call: Log event to Audit Service
        auditServiceClient.recordLog("TAXPAYER_PROFILE_UPDATE", "profile_update/" + taxpayer.getId());

        return getProfile(email);
    }

    @Override
    public List<TaxpayerDocumentResponseDto> getDocuments(String email) {
        Taxpayer taxpayer = getTaxpayerByEmail(email);

        return taxpayerDocumentRepository.findByTaxpayer(taxpayer).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional
    public TaxpayerDocumentResponseDto uploadDocument(String email, String fileUri, DocTypeTaxpayer docType) {
        if (fileUri == null || fileUri.trim().isEmpty()) {
            throw new IllegalArgumentException("File URI is required");
        }

        Taxpayer taxpayer = getTaxpayerByEmail(email);

        boolean exists = taxpayerDocumentRepository.findByTaxpayer(taxpayer).stream()
                .anyMatch(doc -> doc.getDocType().equals(docType));
        if (exists) {
            throw new IllegalStateException("Document of type " + docType + " already exists.");
        }

        TaxpayerDocument document = TaxpayerDocument.builder()
                .taxpayer(taxpayer)
                .docType(docType)
                .fileUri(fileUri)
                .verificationStatus(VerificationStatus.Pending)
                .build();

        TaxpayerDocument savedDocument = taxpayerDocumentRepository.save(document);

        // Microservice Call: Log event to Audit Service
        auditServiceClient.recordLog("UPLOAD_DOCUMENT", "upload_document/" + taxpayer.getId());

        return convertToDto(savedDocument);
    }

    @Override
    @Transactional
    public void deleteDocument(String email, Long documentId) {
        Taxpayer taxpayer = getTaxpayerByEmail(email);
        TaxpayerDocument document = getDocumentById(documentId);

        validateDocumentOwnership(document, taxpayer);

        if (document.getVerificationStatus() != VerificationStatus.Rejected) {
            throw new IllegalStateException("Document can only be deleted if status is Rejected.");
        }

        taxpayerDocumentRepository.delete(document);

        // Microservice Call: Log event to Audit Service
        auditServiceClient.recordLog("DELETE_DOCUMENT", "delete_document/" + taxpayer.getId());
    }

    @Override
    @Transactional
    public TaxpayerDocumentResponseDto updateDocument(String email, Long documentId, String fileUri) {
        if (fileUri == null || fileUri.trim().isEmpty()) {
            throw new IllegalArgumentException("File URI is required");
        }

        Taxpayer taxpayer = getTaxpayerByEmail(email);
        TaxpayerDocument document = getDocumentById(documentId);

        validateDocumentOwnership(document, taxpayer);

        if (document.getVerificationStatus() == VerificationStatus.Rejected) {
            throw new IllegalStateException("Cannot update a rejected document. Please delete and upload again.");
        }

        document.setFileUri(fileUri);
        document.setVerificationStatus(VerificationStatus.Pending);

        TaxpayerDocument updatedDocument = taxpayerDocumentRepository.save(document);

        // Microservice Call: Log event to Audit Service
        auditServiceClient.recordLog("DOCUMENT_UPDATED", "document_updated/" + taxpayer.getId());

        return convertToDto(updatedDocument);
    }

    @Override
    @Transactional
    public TaxpayerDocumentResponseDto verifyDocumentStatus(String email, Long documentId, VerificationStatus verificationStatus) {
        Taxpayer taxpayer = getTaxpayerByEmail(email);
        TaxpayerDocument document = getDocumentById(documentId);

        validateDocumentOwnership(document, taxpayer);

        document.setVerificationStatus(verificationStatus);
        TaxpayerDocument updatedDocument = taxpayerDocumentRepository.save(document);

        // Microservice Call: Log event to Audit Service
        auditServiceClient.recordLog("DOCUMENT_VERIFIED", "Status changed to " + verificationStatus + " for doc: " + documentId);

        return convertToDto(updatedDocument);
    }

    // --- Helper Methods to keep code DRY ---

    private Taxpayer getTaxpayerByEmail(String email) {
        return taxpayerRepository.findByUserEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Taxpayer not found for email: " + email));
    }

    private TaxpayerDocument getDocumentById(Long documentId) {
        return taxpayerDocumentRepository.findById(documentId)
                .orElseThrow(() -> new NoSuchElementException("Document not found with ID: " + documentId));
    }

    private void validateDocumentOwnership(TaxpayerDocument document, Taxpayer taxpayer) {
        if (!document.getTaxpayer().getId().equals(taxpayer.getId())) {
            throw new SecurityException("Document does not belong to this taxpayer");
        }
    }

    private TaxpayerDocumentResponseDto convertToDto(TaxpayerDocument document) {
        return TaxpayerDocumentResponseDto.builder()
                .id(document.getId())
                .docType(document.getDocType())
                .fileUri(document.getFileUri())
                .verificationStatus(document.getVerificationStatus())
                .uploadedDate(document.getUploadedDate())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}