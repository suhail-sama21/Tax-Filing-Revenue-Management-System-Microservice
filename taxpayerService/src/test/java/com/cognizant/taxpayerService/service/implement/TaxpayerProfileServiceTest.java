package com.cognizant.taxpayerService.service.implement;

import com.cognizant.taxpayerService.client.UserServiceClient;
import com.cognizant.taxpayerService.dao.TaxpayerDocumentRepository;
import com.cognizant.taxpayerService.dao.TaxpayerRepository;
import com.cognizant.taxpayerService.dto.DocumentUploadRequestDto;
import com.cognizant.taxpayerService.dto.TaxpayerDocumentResponseDto;
import com.cognizant.taxpayerService.dto.TaxpayerResponse;
import com.cognizant.taxpayerService.dto.UpdateTaxpayerProfileRequestDto;
import com.cognizant.taxpayerService.entity.Taxpayer;
import com.cognizant.taxpayerService.entity.TaxpayerDocument;
import com.cognizant.taxpayerService.exception.GlobalExceptionHandler.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaxpayerProfileServiceTest {

    @Mock
    private TaxpayerRepository taxpayerRepository;

    @Mock
    private TaxpayerDocumentRepository documentRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private TaxpayerProfileService service;

    private Taxpayer taxpayer;
    private TaxpayerDocument document;
    private DocumentUploadRequestDto request;

    @BeforeEach
    void setUp() {
        taxpayer = Taxpayer.builder()
                .userId(1L)
                .taxpayerIdNumber("123456789")
                .type("Citizen")
                .build();

        document = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(taxpayer)
                .docType("IDProof")
                .fileUri("http://example.com/id.pdf")
                .verificationStatus("Pending")
                .build();

        request = DocumentUploadRequestDto.builder()
                .docType("PAN")
                .fileUri("http://example.com/pan.pdf")
                .build();
    }

    @Test
    void testUploadDocument_Success() {
        when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        when(documentRepository.findByTaxpayer(taxpayer)).thenReturn(Arrays.asList());
        when(documentRepository.save(any(TaxpayerDocument.class))).thenReturn(document);

        TaxpayerDocumentResponseDto result = service.uploadDocument(1L, request);

        assertNotNull(result);
        assertEquals("IDProof", result.getDocType());
        verify(documentRepository).save(any(TaxpayerDocument.class));
    }

    @Test
    void testUploadDocument_DocumentTypeAlreadyExists() {
        TaxpayerDocument existingDoc = TaxpayerDocument.builder()
                .docType("PAN")
                .build();
        when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        when(documentRepository.findByTaxpayer(taxpayer)).thenReturn(Arrays.asList(existingDoc));

        assertThrows(DocumentTypeAlreadyExistsException.class, () -> service.uploadDocument(1L, request));
    }

    @Test
    void testUploadDocument_MaximumDocumentsExceeded() {
        List<TaxpayerDocument> existingDocs = Arrays.asList(
                TaxpayerDocument.builder().docType("IDProof").build(),
                TaxpayerDocument.builder().docType("PAN").build()
        );
        when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        when(documentRepository.findByTaxpayer(taxpayer)).thenReturn(existingDocs);

        DocumentUploadRequestDto maxRequest = DocumentUploadRequestDto.builder()
                .docType("AddressProof")
                .fileUri("http://example.com/address.pdf")
                .build();

        assertThrows(MaximumDocumentsExceededException.class, () -> service.uploadDocument(1L, maxRequest));
    }

    @Test
    void testUploadDocument_TaxpayerNotFound() {
        when(taxpayerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TaxpayerNotFoundException.class, () -> service.uploadDocument(1L, request));
    }

    @Test
    void testUpdateDocument_Success() {
        when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentRepository.findByTaxpayer(taxpayer)).thenReturn(Arrays.asList(document));
        when(documentRepository.save(any(TaxpayerDocument.class))).thenReturn(document);

        TaxpayerDocumentResponseDto result = service.updateDocument(1L, 1L, request);

        assertNotNull(result);
        assertEquals("Pending", document.getVerificationStatus());
        verify(documentRepository).save(document);
    }

    @Test
    void testUpdateDocument_DocumentNotFound() {
        when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        when(documentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DocumentNotFoundException.class, () -> service.updateDocument(1L, 1L, request));
    }

    @Test
    void testUpdateDocument_OwnershipException() {
        Taxpayer otherTaxpayer = Taxpayer.builder().userId(2L).build();
        document.setTaxpayer(otherTaxpayer);
        when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        assertThrows(DocumentOwnershipException.class, () -> service.updateDocument(1L, 1L, request));
    }

    @Test
    void testUpdateDocument_TypeAlreadyExists() {
        TaxpayerDocument existingDoc = TaxpayerDocument.builder()
                .id(2L)
                .docType("PAN")
                .build();
        document.setDocType("IDProof");
        when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentRepository.findByTaxpayer(taxpayer)).thenReturn(Arrays.asList(document, existingDoc));

        assertThrows(DocumentTypeAlreadyExistsException.class, () -> service.updateDocument(1L, 1L, request));
    }

    @Test
    void testGetDocuments() {
        when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        when(documentRepository.findByTaxpayer(taxpayer)).thenReturn(Arrays.asList(document));

        List<TaxpayerDocumentResponseDto> result = service.getDocuments(1L);

        assertEquals(1, result.size());
        assertEquals("IDProof", result.get(0).getDocType());
    }

    @Test
    void testDeleteDocument_Success() {
        document.setVerificationStatus("Rejected");
        lenient().when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        lenient().when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        service.deleteDocument(1L, 1L);

        verify(documentRepository).delete(document);
    }

    @Test
    void testDeleteDocument_NotRejected() {
        document.setVerificationStatus("Approved");
        lenient().when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        lenient().when(documentRepository.findById(1L)).thenReturn(Optional.of(document));

        assertThrows(DocumentDeletionException.class, () -> service.deleteDocument(1L, 1L));
    }

    @Test
    void testUpdateDocumentStatus_Success() {
        when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
        when(documentRepository.save(any(TaxpayerDocument.class))).thenReturn(document);

        service.updateDocumentStatus(1L, 1L, "Approved");

        assertEquals("Approved", document.getVerificationStatus());
        verify(documentRepository).save(document);
    }
}