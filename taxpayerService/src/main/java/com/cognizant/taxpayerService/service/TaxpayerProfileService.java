package com.cognizant.taxpayerService.service;

import com.cognizant.taxpayerService.client.UserServiceClient;
import com.cognizant.taxpayerService.dao.TaxpayerDocumentRepository;
import com.cognizant.taxpayerService.dao.TaxpayerRepository;
import com.cognizant.taxpayerService.dto.*;
import com.cognizant.taxpayerService.entity.Taxpayer;
import com.cognizant.taxpayerService.entity.TaxpayerDocument;
import com.cognizant.taxpayerService.exception.GlobalExceptionHandler.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxpayerProfileService {

    private final TaxpayerRepository taxpayerRepository;
    private final TaxpayerDocumentRepository documentRepository;
    private final UserServiceClient userServiceClient;

    // --- PROFILE LOGIC ---

    @Transactional
    public Taxpayer createBaseProfile(Long userId, String type) throws RuntimeException {
        log.info("Creating base tax profile for User ID: {}", userId);
        UserDto userDto=userServiceClient.getUserById(userId);
        if(userDto==null)throw new RuntimeException("User not found in User Service for ID: "+userId);
        Taxpayer taxpayer = Taxpayer.builder()
                .userId(userId) // Setting the ID explicitly
                .type(type != null ? type : "Citizen")
                .taxpayerIdNumber(generateUniqueTaxpayerId())
                .build();

        return taxpayerRepository.save(taxpayer);
    }

    public TaxpayerResponse getFullTaxpayerProfile(Long userId) {
        log.info("Fetching full profile for User ID: {}", userId);

        // 1. Get Local Tax Data (using findById since userId is the primary key!)
        Taxpayer taxpayer = taxpayerRepository.findById(userId)
                .orElseThrow(() -> new TaxpayerNotFoundException("Taxpayer not found for user ID: " + userId));

        // 2. Get Remote User Data via Feign
        UserDto userDto;
        try {
            userDto = userServiceClient.getUserById(userId);
        } catch (Exception e) {
            throw new UserServiceException("Failed to retrieve user data from User Service for user ID: " + userId, e);
        }

        // 3. Combine
        return TaxpayerResponse.builder()
                .taxpayerId(taxpayer.getUserId())
                .taxpayerIdNumber(taxpayer.getTaxpayerIdNumber())
                .type(taxpayer.getType())
                .user(userDto)
                .build();
    }

    public TaxpayerResponse updateProfile(Long userId, UpdateTaxpayerProfileRequestDto request) {
        log.info("Forwarding profile update for User ID: {} to User Service", userId);
        try {
            userServiceClient.updateUserProfile(userId, request);
        } catch (Exception e) {
            throw new UserServiceException("Failed to update user profile in User Service for user ID: " + userId, e);
        }
        return getFullTaxpayerProfile(userId);
    }

    // --- DOCUMENT LOGIC ---

    public List<TaxpayerDocumentResponseDto> getDocuments(Long userId) {
        Taxpayer taxpayer = taxpayerRepository.findById(userId).orElseThrow(() -> new TaxpayerNotFoundException("Taxpayer not found for user ID: " + userId));
        return documentRepository.findByTaxpayer(taxpayer).stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Transactional
    public TaxpayerDocumentResponseDto uploadDocument(Long userId, DocumentUploadRequestDto request) {
        Taxpayer taxpayer = taxpayerRepository.findById(userId).orElseThrow(() -> new TaxpayerNotFoundException("Taxpayer not found for user ID: " + userId));
        TaxpayerDocument document = TaxpayerDocument.builder()
                .taxpayer(taxpayer).docType(request.getDocType()).fileUri(request.getFileUri()).verificationStatus("Pending").build();
        return convertToDto(documentRepository.save(document));
    }

    @Transactional
    public void deleteDocument(Long userId, Long documentId) {
        TaxpayerDocument document = documentRepository.findById(documentId).orElseThrow(() -> new DocumentNotFoundException("Document not found with ID: " + documentId));
        if ("Rejected".equalsIgnoreCase(document.getVerificationStatus())) {
            documentRepository.delete(document);
        } else {
            throw new DocumentDeletionException("Only rejected documents can be deleted.");
        }
    }

    private String generateUniqueTaxpayerId() {
        SecureRandom random = new SecureRandom();
        String id;
        do {
            id = String.format("%011d", random.nextInt(1000000000) + 1000000000L);
        } while (taxpayerRepository.existsByTaxpayerIdNumber(id)); // Assuming you kept this custom method in your DAO
        return id;
    }

    private TaxpayerDocumentResponseDto convertToDto(TaxpayerDocument document) {
        return TaxpayerDocumentResponseDto.builder()
                .id(document.getId())
                .docType(document.getDocType())
                .fileUri(document.getFileUri())
                .verificationStatus(document.getVerificationStatus())
                .uploadedDate(document.getUploadedDate()) // <-- ADD THIS LINE
                .build();
    }
    @Transactional
    public TaxpayerDocumentResponseDto updateDocumentStatus(Long userId, Long documentId, String newStatus) {
        log.info("Updating verification status for doc {} to {}", documentId, newStatus);

        Taxpayer taxpayer = taxpayerRepository.findById(userId)
                .orElseThrow(() -> new TaxpayerNotFoundException("Taxpayer not found for user ID: " + userId));

        TaxpayerDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new DocumentNotFoundException("Document not found with ID: " + documentId));

        if (!document.getTaxpayer().getUserId().equals(taxpayer.getUserId())) {
            throw new DocumentOwnershipException("Document does not belong to this taxpayer");
        }

        document.setVerificationStatus(newStatus);
        TaxpayerDocument updatedDocument = documentRepository.save(document);

        return convertToDto(updatedDocument);
    }
}