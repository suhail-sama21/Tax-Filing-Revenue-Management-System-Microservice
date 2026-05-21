package com.cognizant.taxpayerService.service.implement;

import com.cognizant.taxpayerService.client.UserServiceClient;
import com.cognizant.taxpayerService.dao.TaxpayerDocumentRepository;
import com.cognizant.taxpayerService.dao.TaxpayerRepository;
import com.cognizant.taxpayerService.dto.DocumentUploadRequestDto;
import com.cognizant.taxpayerService.dto.TaxpayerDocumentResponseDto;
import com.cognizant.taxpayerService.dto.TaxpayerPendingDocumentDto;
import com.cognizant.taxpayerService.dto.TaxpayerResponse;
import com.cognizant.taxpayerService.dto.UpdateTaxpayerProfileRequestDto;
import com.cognizant.taxpayerService.dto.UserDto;
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
                TaxpayerDocument.builder().id(10L).docType("IDProof").build(),
                TaxpayerDocument.builder().id(11L).docType("PAN").build(),
                TaxpayerDocument.builder().id(12L).docType("AddressProof").build()
        );
        when(taxpayerRepository.findById(1L)).thenReturn(Optional.of(taxpayer));
        when(documentRepository.findByTaxpayer(taxpayer)).thenReturn(existingDocs);

        DocumentUploadRequestDto maxRequest = DocumentUploadRequestDto.builder()
                .docType("DriversLicense")
                .fileUri("http://example.com/license.pdf")
                .build();

        assertThrows(MaximumDocumentsExceededException.class, () -> service.uploadDocument(1L, maxRequest));
        verify(documentRepository, never()).save(any(TaxpayerDocument.class));
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

    @Test
    void testGetTaxpayersWithPendingDocuments_Success() {
        Taxpayer taxpayer2 = Taxpayer.builder()
                .userId(2L)
                .taxpayerIdNumber("987654321")
                .type("Citizen")
                .build();

        TaxpayerDocument doc1 = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(taxpayer)
                .docType("IDProof")
                .fileUri("http://example.com/id.pdf")
                .verificationStatus("Pending")
                .build();

        TaxpayerDocument doc2 = TaxpayerDocument.builder()
                .id(2L)
                .taxpayer(taxpayer)
                .docType("PAN")
                .fileUri("http://example.com/pan.pdf")
                .verificationStatus("Verified")
                .build();

        TaxpayerDocument doc3 = TaxpayerDocument.builder()
                .id(3L)
                .taxpayer(taxpayer2)
                .docType("AddressProof")
                .fileUri("http://example.com/address.pdf")
                .verificationStatus("Pending")
                .build();

        UserDto userDto1 = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .panNumber("ABCDE1234F")
                .build();

        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name("Jane Smith")
                .email("jane@example.com")
                .panNumber("XYZAB5678M")
                .build();

        when(documentRepository.findByVerificationStatusIn(Arrays.asList("Pending", "Rejected")))
                .thenReturn(Arrays.asList(doc1, doc3));
        when(userServiceClient.getUserById(1L)).thenReturn(userDto1);
        when(userServiceClient.getUserById(2L)).thenReturn(userDto2);

        List<TaxpayerPendingDocumentDto> result = service.getTaxpayersWithPendingDocuments();

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).getUserId());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Pending", result.get(0).getVerificationStatus());
        assertEquals(2L, result.get(1).getUserId());
        assertEquals("Jane Smith", result.get(1).getName());
        assertEquals("Pending", result.get(1).getVerificationStatus());
    }

    @Test
    void testGetTaxpayersWithPendingDocuments_MultipleDocumentsAggregation() {
        TaxpayerDocument doc1 = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(taxpayer)
                .docType("IDProof")
                .fileUri("http://example.com/id.pdf")
                .verificationStatus("Pending")
                .build();

        TaxpayerDocument doc2 = TaxpayerDocument.builder()
                .id(2L)
                .taxpayer(taxpayer)
                .docType("PAN")
                .fileUri("http://example.com/pan.pdf")
                .verificationStatus("Verified")
                .build();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .panNumber("ABCDE1234F")
                .build();

        when(documentRepository.findByVerificationStatusIn(Arrays.asList("Pending", "Rejected")))
                .thenReturn(Arrays.asList(doc1));
        when(userServiceClient.getUserById(1L)).thenReturn(userDto);

        List<TaxpayerPendingDocumentDto> result = service.getTaxpayersWithPendingDocuments();

        assertEquals(1, result.size());
        assertEquals("Pending", result.get(0).getVerificationStatus());
        verify(userServiceClient).getUserById(1L);
    }

    @Test
    void testGetTaxpayersWithPendingDocuments_Empty() {
        when(documentRepository.findByVerificationStatusIn(Arrays.asList("Pending", "Rejected")))
                .thenReturn(Arrays.asList());

        List<TaxpayerPendingDocumentDto> result = service.getTaxpayersWithPendingDocuments();

        assertEquals(0, result.size());
        verify(documentRepository).findByVerificationStatusIn(Arrays.asList("Pending", "Rejected"));
    }
}