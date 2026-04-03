package com.cognizant.taxpayerService.service;

import com.cognizant.taxpayerService.dao.TaxpayerDocumentRepository;
import com.cognizant.taxpayerService.dao.TaxpayerRepository;
import com.cognizant.taxpayerService.dao.UserRepository;
import com.cognizant.taxpayerService.dto.responsedto.TaxpayerDocumentResponseDto;
import com.cognizant.taxpayerService.dto.responsedto.TaxpayerProfileResponseDto;
import com.cognizant.taxpayerService.dto.requestdto.UpdateTaxpayerProfileRequestDto;
import com.cognizant.taxpayerService.entity.Taxpayer;
import com.cognizant.taxpayerService.entity.TaxpayerDocument;
import com.cognizant.taxpayerService.entity.User;
import com.cognizant.taxpayerService.entity.entityEnum.DocTypeTaxpayer;
import com.cognizant.taxpayerService.entity.entityEnum.StatusBasic;
import com.cognizant.taxpayerService.entity.entityEnum.TaxpayerType;
import com.cognizant.taxpayerService.entity.entityEnum.UserRole;
import com.cognizant.taxpayerService.entity.entityEnum.VerificationStatus;
import com.cognizant.taxpayerService.service.impl.TaxpayerProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Taxpayer Profile Service Tests")
class TaxpayerProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaxpayerRepository taxpayerRepository;

    @Mock
    private TaxpayerDocumentRepository taxpayerDocumentRepository;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private TaxpayerProfileServiceImpl profileService;

    private User mockUser;
    private Taxpayer mockTaxpayer;
    private UpdateTaxpayerProfileRequestDto updateRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = User.builder()
                .id(1L)
                .name("John Doe")
                .email("john@example.com")
                .phone("+911234567890")
                .passwordHash("hashedPassword")
                .role(UserRole.TAXPAYER)
                .status(StatusBasic.Active)
                .build();

        mockTaxpayer = Taxpayer.builder()
                .id(1L)
                .user(mockUser)
                .name("John Doe")
                .type(TaxpayerType.Citizen)
                .taxpayerIdNumber("12345678901")
                .address("123 Main Street, New York, NY 10001")
                .contactInfo("john.doe@example.com | +1-234-567-8901")
                .build();

        updateRequest = UpdateTaxpayerProfileRequestDto.builder()
                .address("456 Oak Avenue, Los Angeles, CA 90001")
                .contactInfo("john.doe.updated@example.com | +1-987-654-3210")
                .build();
    }

    // ==================== Get Profile Tests ====================

    @Test
    @DisplayName("Should successfully get taxpayer profile")
    void testGetProfileSuccess() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));

        // Act
        TaxpayerProfileResponseDto profile = profileService.getProfile("john@example.com");

        // Assert
        assertNotNull(profile);
        assertEquals("John Doe", profile.getName());
        assertEquals("john@example.com", profile.getEmail());
        assertEquals("+911234567890", profile.getPhone());
        assertEquals("12345678901", profile.getTaxpayerIdNumber());
        assertEquals(TaxpayerType.Citizen, profile.getType());
        verify(userRepository, times(1)).findByEmail("john@example.com");
        verify(taxpayerRepository, times(1)).findByUser(mockUser);
    }

    @Test
    @DisplayName("Should throw exception when user not found for getProfile")
    void testGetProfileUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            profileService.getProfile("nonexistent@example.com");
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when taxpayer not found for getProfile")
    void testGetProfileTaxpayerNotFound() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            profileService.getProfile("john@example.com");
        });
        assertEquals("Taxpayer not found", exception.getMessage());
    }

    // ==================== Update Profile Tests ====================

    @Test
    @DisplayName("Should successfully update taxpayer profile")
    void testUpdateProfileSuccess() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerRepository.save(any(Taxpayer.class))).thenReturn(mockTaxpayer);

        // Act
        TaxpayerProfileResponseDto profile = profileService.updateProfile("john@example.com", updateRequest);

        // Assert
        assertNotNull(profile);
        verify(taxpayerRepository, times(1)).save(any(Taxpayer.class));
    }

    @Test
    @DisplayName("Should update address correctly")
    void testUpdateProfileAddress() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerRepository.save(any(Taxpayer.class))).thenAnswer(invocation -> {
            Taxpayer arg = invocation.getArgument(0);
            mockTaxpayer.setAddress(arg.getAddress());
            return mockTaxpayer;
        });

        // Act
        profileService.updateProfile("john@example.com", updateRequest);

        // Assert
        assertEquals(updateRequest.getAddress(), mockTaxpayer.getAddress());
    }

    @Test
    @DisplayName("Should update contact info correctly")
    void testUpdateProfileContactInfo() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerRepository.save(any(Taxpayer.class))).thenAnswer(invocation -> {
            Taxpayer arg = invocation.getArgument(0);
            mockTaxpayer.setContactInfo(arg.getContactInfo());
            return mockTaxpayer;
        });

        // Act
        profileService.updateProfile("john@example.com", updateRequest);

        // Assert
        assertEquals(updateRequest.getContactInfo(), mockTaxpayer.getContactInfo());
    }

    @Test
    @DisplayName("Should throw exception when user not found for updateProfile")
    void testUpdateProfileUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            profileService.updateProfile("nonexistent@example.com", updateRequest);
        });
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when taxpayer not found for updateProfile")
    void testUpdateProfileTaxpayerNotFound() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            profileService.updateProfile("john@example.com", updateRequest);
        });
        assertEquals("Taxpayer not found", exception.getMessage());
    }

    // ==================== Get Documents Tests ====================

    @Test
    @DisplayName("Should get all documents for a taxpayer")
    void testGetDocumentsSuccess() {
        // Arrange
        List<TaxpayerDocument> documents = new ArrayList<>();
        TaxpayerDocument doc1 = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(mockTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .fileUri("https://drive.google.com/file/d/1abc123/view")
                .verificationStatus(VerificationStatus.Pending)
                .uploadedDate(Instant.now())
                .updatedAt(Instant.now())
                .build();
        documents.add(doc1);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findByTaxpayer(mockTaxpayer)).thenReturn(documents);

        // Act
        List<TaxpayerDocumentResponseDto> result = profileService.getDocuments("john@example.com");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(DocTypeTaxpayer.IDProof, result.get(0).getDocType());
    }

    @Test
    @DisplayName("Should return empty list when no documents exist")
    void testGetDocumentsEmpty() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findByTaxpayer(mockTaxpayer)).thenReturn(new ArrayList<>());

        // Act
        List<TaxpayerDocumentResponseDto> result = profileService.getDocuments("john@example.com");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when user not found for getDocuments")
    void testGetDocumentsUserNotFound() {
        // Arrange
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            profileService.getDocuments("nonexistent@example.com");
        });
        assertEquals("User not found", exception.getMessage());
    }

    // ==================== Upload Document Tests ====================

    @Test
    @DisplayName("Should successfully upload a document")
    void testUploadDocumentSuccess() {
        // Arrange
        TaxpayerDocument newDoc = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(mockTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .fileUri("https://drive.google.com/file/d/1abc123/view")
                .verificationStatus(VerificationStatus.Pending)
                .uploadedDate(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findByTaxpayer(mockTaxpayer)).thenReturn(new ArrayList<>());
        when(taxpayerDocumentRepository.save(any(TaxpayerDocument.class))).thenReturn(newDoc);

        // Act
        TaxpayerDocumentResponseDto result = profileService.uploadDocument(
                "john@example.com",
                "https://drive.google.com/file/d/1abc123/view",
                DocTypeTaxpayer.IDProof
        );

        // Assert
        assertNotNull(result);
        assertEquals(DocTypeTaxpayer.IDProof, result.getDocType());
        assertEquals(VerificationStatus.Pending, result.getVerificationStatus());
    }

    @Test
    @DisplayName("Should throw exception when file URI is empty")
    void testUploadDocumentEmptyFileUri() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.uploadDocument("john@example.com", "", DocTypeTaxpayer.IDProof);
        });
        assertEquals("File URI is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when file URI is null")
    void testUploadDocumentNullFileUri() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.uploadDocument("john@example.com", null, DocTypeTaxpayer.IDProof);
        });
        assertEquals("File URI is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when document type already exists")
    void testUploadDocumentDuplicate() {
        // Arrange
        TaxpayerDocument existingDoc = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(mockTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .fileUri("https://drive.google.com/file/d/1existing/view")
                .verificationStatus(VerificationStatus.Pending)
                .build();

        List<TaxpayerDocument> documents = new ArrayList<>();
        documents.add(existingDoc);

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findByTaxpayer(mockTaxpayer)).thenReturn(documents);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.uploadDocument(
                    "john@example.com",
                    "https://drive.google.com/file/d/1new/view",
                    DocTypeTaxpayer.IDProof
            );
        });
        assertTrue(exception.getMessage().contains("already exists"));
    }

    // ==================== Delete Document Tests ====================

    @Test
    @DisplayName("Should successfully delete a rejected document")
    void testDeleteDocumentSuccess() {
        // Arrange
        TaxpayerDocument doc = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(mockTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .fileUri("https://drive.google.com/file/d/1abc123/view")
                .verificationStatus(VerificationStatus.Rejected)
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findById(1L)).thenReturn(Optional.of(doc));

        // Act
        profileService.deleteDocument("john@example.com", 1L);

        // Assert
        verify(taxpayerDocumentRepository, times(1)).delete(doc);
    }

    @Test
    @DisplayName("Should throw exception when document not found for deletion")
    void testDeleteDocumentNotFound() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.deleteDocument("john@example.com", 999L);
        });
        assertEquals("Document not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when document belongs to different taxpayer")
    void testDeleteDocumentNotBelongsToTaxpayer() {
        // Arrange
        User otherUser = User.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .build();

        Taxpayer otherTaxpayer = Taxpayer.builder()
                .id(2L)
                .user(otherUser)
                .taxpayerIdNumber("98765432109")
                .build();

        TaxpayerDocument doc = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(otherTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .verificationStatus(VerificationStatus.Rejected)
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findById(1L)).thenReturn(Optional.of(doc));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.deleteDocument("john@example.com", 1L);
        });
        assertEquals("Document does not belong to this taxpayer", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when deleting non-rejected document")
    void testDeleteDocumentNotRejected() {
        // Arrange
        TaxpayerDocument doc = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(mockTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .verificationStatus(VerificationStatus.Pending)
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findById(1L)).thenReturn(Optional.of(doc));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.deleteDocument("john@example.com", 1L);
        });
        assertTrue(exception.getMessage().contains("can only be deleted if status is Rejected"));
    }

    // ==================== Update Document Tests ====================

    @Test
    @DisplayName("Should successfully update a document")
    void testUpdateDocumentSuccess() {
        // Arrange
        TaxpayerDocument doc = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(mockTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .fileUri("https://drive.google.com/file/d/1abc123/view")
                .verificationStatus(VerificationStatus.Pending)
                .uploadedDate(Instant.now())
                .updatedAt(Instant.now())
                .build();

        TaxpayerDocument updatedDoc = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(mockTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .fileUri("https://drive.google.com/file/d/1new/view")
                .verificationStatus(VerificationStatus.Pending)
                .uploadedDate(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(taxpayerDocumentRepository.save(any(TaxpayerDocument.class))).thenReturn(updatedDoc);

        // Act
        TaxpayerDocumentResponseDto result = profileService.updateDocument(
                "john@example.com",
                1L,
                "https://drive.google.com/file/d/1new/view"
        );

        // Assert
        assertNotNull(result);
        assertEquals("https://drive.google.com/file/d/1new/view", result.getFileUri());
        assertEquals(VerificationStatus.Pending, result.getVerificationStatus());
    }

    @Test
    @DisplayName("Should reset verification status to Pending after update")
    void testUpdateDocumentResetsStatus() {
        // Arrange
        TaxpayerDocument doc = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(mockTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .fileUri("https://drive.google.com/file/d/1abc123/view")
                .verificationStatus(VerificationStatus.Verified)
                .build();

        TaxpayerDocument updatedDoc = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(mockTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .fileUri("https://drive.google.com/file/d/1new/view")
                .verificationStatus(VerificationStatus.Pending)
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findById(1L)).thenReturn(Optional.of(doc));
        when(taxpayerDocumentRepository.save(any(TaxpayerDocument.class))).thenReturn(updatedDoc);

        // Act
        TaxpayerDocumentResponseDto result = profileService.updateDocument(
                "john@example.com",
                1L,
                "https://drive.google.com/file/d/1new/view"
        );

        // Assert
        assertEquals(VerificationStatus.Pending, result.getVerificationStatus());
    }

    @Test
    @DisplayName("Should throw exception when file URI is empty for update")
    void testUpdateDocumentEmptyFileUri() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.updateDocument("john@example.com", 1L, "");
        });
        assertEquals("File URI is required", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when document not found for update")
    void testUpdateDocumentNotFound() {
        // Arrange
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.updateDocument("john@example.com", 999L, "https://drive.google.com/file/d/1new/view");
        });
        assertEquals("Document not found", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when document belongs to different taxpayer for update")
    void testUpdateDocumentNotBelongsToTaxpayer() {
        // Arrange
        User otherUser = User.builder()
                .id(2L)
                .name("Jane Doe")
                .email("jane@example.com")
                .build();

        Taxpayer otherTaxpayer = Taxpayer.builder()
                .id(2L)
                .user(otherUser)
                .taxpayerIdNumber("98765432109")
                .build();

        TaxpayerDocument doc = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(otherTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .verificationStatus(VerificationStatus.Pending)
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findById(1L)).thenReturn(Optional.of(doc));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.updateDocument("john@example.com", 1L, "https://drive.google.com/file/d/1new/view");
        });
        assertEquals("Document does not belong to this taxpayer", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when updating rejected document")
    void testUpdateDocumentRejected() {
        // Arrange
        TaxpayerDocument doc = TaxpayerDocument.builder()
                .id(1L)
                .taxpayer(mockTaxpayer)
                .docType(DocTypeTaxpayer.IDProof)
                .verificationStatus(VerificationStatus.Rejected)
                .build();

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(mockUser));
        when(taxpayerRepository.findByUser(mockUser)).thenReturn(Optional.of(mockTaxpayer));
        when(taxpayerDocumentRepository.findById(1L)).thenReturn(Optional.of(doc));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            profileService.updateDocument("john@example.com", 1L, "https://drive.google.com/file/d/1new/view");
        });
        assertEquals("Cannot update a rejected document. Please delete and upload again.", exception.getMessage());
    }
}
