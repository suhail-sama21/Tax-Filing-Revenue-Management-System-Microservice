package com.cognizant.paymentService.service.impl;

import com.cognizant.taxease.dao.TaxpayerDocumentRepository;
import com.cognizant.taxease.dao.TaxpayerRepository;
import com.cognizant.taxease.dao.UserRepository;
import com.cognizant.taxease.dto.responsedto.TaxpayerDocumentResponseDto;
import com.cognizant.taxease.dto.responsedto.TaxpayerProfileResponseDto;
import com.cognizant.taxease.dto.requestdto.UpdateTaxpayerProfileRequestDto;
import com.cognizant.taxease.entity.Taxpayer;
import com.cognizant.taxease.entity.TaxpayerDocument;
import com.cognizant.taxease.entity.User;
import com.cognizant.taxease.entity.entityEnum.DocTypeTaxpayer;
import com.cognizant.taxease.entity.entityEnum.VerificationStatus;
import com.cognizant.taxease.service.AuditLogService;
import com.cognizant.taxease.service.TaxpayerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TaxpayerProfileServiceImpl implements TaxpayerProfileService {

    private final UserRepository userRepository;
    private final TaxpayerRepository taxpayerRepository;
    private final TaxpayerDocumentRepository taxpayerDocumentRepository;
    private final AuditLogService auditLogService;

    @Override
    public TaxpayerProfileResponseDto getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Taxpayer taxpayer = taxpayerRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("Taxpayer not found"));

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
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Taxpayer taxpayer = taxpayerRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("Taxpayer not found"));

        taxpayer.setAddress(request.getAddress());
        taxpayer.setContactInfo(request.getContactInfo());
        taxpayerRepository.save(taxpayer);
        auditLogService.record("TAXPAYER_PROFILE_UPDATE", "profile_update/" + taxpayer.getId());
        return getProfile(email); // Return updated profile
    }

    @Override
    public List<TaxpayerDocumentResponseDto> getDocuments(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Taxpayer taxpayer = taxpayerRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("Taxpayer not found"));

        return taxpayerDocumentRepository.findByTaxpayer(taxpayer).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Override
    @Transactional
    public TaxpayerDocumentResponseDto uploadDocument(String email, String fileUri, DocTypeTaxpayer docType) {
        if (fileUri == null || fileUri.trim().isEmpty()) {
            throw new RuntimeException("File URI is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Taxpayer taxpayer = taxpayerRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("Taxpayer not found"));

        // Check if document type already exists
        boolean exists = taxpayerDocumentRepository.findByTaxpayer(taxpayer).stream()
                .anyMatch(doc -> doc.getDocType().equals(docType));
        if (exists) {
            throw new RuntimeException("Document of type " + docType + " already exists. Delete the existing one to upload again.");
        }

        // Save document entity with the provided URI
        TaxpayerDocument document = TaxpayerDocument.builder()
                .taxpayer(taxpayer)
                .docType(docType)
                .fileUri(fileUri)
                .verificationStatus(VerificationStatus.Pending)
                .build();

        TaxpayerDocument savedDocument = taxpayerDocumentRepository.save(document);
        auditLogService.record("UPLOAD_DOCUMENT", "upload_document/" + taxpayer.getId());
        return convertToDto(savedDocument);
    }

    @Override
    @Transactional
    public void deleteDocument(String email, Long documentId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Taxpayer taxpayer = taxpayerRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("Taxpayer not found"));

        TaxpayerDocument document = taxpayerDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!document.getTaxpayer().equals(taxpayer)) {
            throw new RuntimeException("Document does not belong to this taxpayer");
        }

        if (document.getVerificationStatus() != VerificationStatus.Rejected) {
            throw new RuntimeException("Document can only be deleted if status is Rejected. Current status: " + document.getVerificationStatus());
        }

        // No file deletion needed since we're storing links, not files
        taxpayerDocumentRepository.delete(document);
        auditLogService.record("DELETE_DOCUMENT", "delete_document/" + taxpayer.getId());
    }

    @Override
    @Transactional
    public TaxpayerDocumentResponseDto updateDocument(String email, Long documentId, String fileUri) {
        if (fileUri == null || fileUri.trim().isEmpty()) {
            throw new RuntimeException("File URI is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        Taxpayer taxpayer = taxpayerRepository.findByUser(user)
                .orElseThrow(() -> new NoSuchElementException("Taxpayer not found"));

        TaxpayerDocument document = taxpayerDocumentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        if (!document.getTaxpayer().equals(taxpayer)) {
            throw new RuntimeException("Document does not belong to this taxpayer");
        }

        if (document.getVerificationStatus() == VerificationStatus.Rejected) {
            throw new RuntimeException("Cannot update a rejected document. Please delete and upload again.");
        }

        // Update document with new URI
        document.setFileUri(fileUri);
        document.setVerificationStatus(VerificationStatus.Pending); // Reset to pending after update

        TaxpayerDocument updatedDocument = taxpayerDocumentRepository.save(document);
        auditLogService.record("DOCUMENT_UPDATED", "document_updated/" + taxpayer.getId());
        return convertToDto(updatedDocument);
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