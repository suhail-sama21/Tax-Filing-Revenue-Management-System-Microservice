package com.cognizant.auditservice.service;

import com.cognizant.auditservice.dto.AuditResponse;
import com.cognizant.auditservice.dto.CloseAuditRequest;
import com.cognizant.auditservice.dto.CreateAuditRequest;
import com.cognizant.auditservice.entity.Audit;
import com.cognizant.auditservice.entityenum.StatusBasic;
import com.cognizant.auditservice.repository.AuditRepository;
import com.cognizant.auditservice.service.impl.AuditServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditServiceImplTest {

    @Mock
    private AuditRepository auditRepository;

    @InjectMocks
    private AuditServiceImpl auditService;

    @Test
    void shouldCreateAuditSuccessfully() {
        CreateAuditRequest request = CreateAuditRequest.builder()
                .officerId(101L)
                .scope("Tax Fraud Check")
                .findings("Suspicious entries found")
                .build();

        Audit savedAudit = Audit.builder()
                .id(1L)
                .officerId(101L)
                .scope("Tax Fraud Check")
                .findings("Suspicious entries found")
                .status(StatusBasic.Active)
                .createdAt(Instant.now())
                .build();

        when(auditRepository.save(any(Audit.class))).thenReturn(savedAudit);

        AuditResponse response = auditService.createAudit(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(101L, response.getOfficerId());
        assertEquals("Tax Fraud Check", response.getScope());
        assertEquals("Suspicious entries found", response.getFindings());
        assertEquals(StatusBasic.Active, response.getStatus());

        verify(auditRepository, times(1)).save(any(Audit.class));
    }

    @Test
    void shouldCreateAuditWithDefaultFindingsWhenFindingsIsNull() {
        CreateAuditRequest request = CreateAuditRequest.builder()
                .officerId(102L)
                .scope("Compliance Check")
                .findings(null)
                .build();

        Audit savedAudit = Audit.builder()
                .id(2L)
                .officerId(102L)
                .scope("Compliance Check")
                .findings("Auto-detected issue")
                .status(StatusBasic.Active)
                .createdAt(Instant.now())
                .build();

        when(auditRepository.save(any(Audit.class))).thenReturn(savedAudit);

        AuditResponse response = auditService.createAudit(request);

        assertNotNull(response);
        assertEquals("Auto-detected issue", response.getFindings());
        assertEquals(StatusBasic.Active, response.getStatus());

        verify(auditRepository, times(1)).save(any(Audit.class));
    }

    @Test
    void shouldReturnAllAudits() {
        Audit audit1 = Audit.builder()
                .id(1L)
                .officerId(101L)
                .scope("Scope 1")
                .findings("Finding 1")
                .status(StatusBasic.Active)
                .createdAt(Instant.now())
                .build();

        Audit audit2 = Audit.builder()
                .id(2L)
                .officerId(102L)
                .scope("Scope 2")
                .findings("Finding 2")
                .status(StatusBasic.Inactive)
                .createdAt(Instant.now())
                .build();

        when(auditRepository.findAll()).thenReturn(List.of(audit1, audit2));

        List<AuditResponse> responses = auditService.getAllAudits();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(101L, responses.get(0).getOfficerId());
        assertEquals(102L, responses.get(1).getOfficerId());

        verify(auditRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnAuditById() {
        Audit audit = Audit.builder()
                .id(1L)
                .officerId(201L)
                .scope("Internal Review")
                .findings("Minor issue")
                .status(StatusBasic.Active)
                .createdAt(Instant.now())
                .build();

        when(auditRepository.findById(1L)).thenReturn(Optional.of(audit));

        AuditResponse response = auditService.getAuditById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(201L, response.getOfficerId());
        assertEquals("Internal Review", response.getScope());

        verify(auditRepository, times(1)).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenAuditNotFoundById() {
        when(auditRepository.findById(99L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> auditService.getAuditById(99L)
        );

        assertEquals("Audit not found", exception.getMessage());
        verify(auditRepository, times(1)).findById(99L);
    }

    @Test
    void shouldCloseAuditSuccessfullyWithUpdatedFindings() {
        CloseAuditRequest request = CloseAuditRequest.builder()
                .findings("Audit completed and closed")
                .build();

        Audit existingAudit = Audit.builder()
                .id(1L)
                .officerId(301L)
                .scope("Yearly Audit")
                .findings("Initial finding")
                .status(StatusBasic.Active)
                .createdAt(Instant.now())
                .build();

        Audit savedAudit = Audit.builder()
                .id(1L)
                .officerId(301L)
                .scope("Yearly Audit")
                .findings("Audit completed and closed")
                .status(StatusBasic.Inactive)
                .createdAt(existingAudit.getCreatedAt())
                .build();

        when(auditRepository.findById(1L)).thenReturn(Optional.of(existingAudit));
        when(auditRepository.save(any(Audit.class))).thenReturn(savedAudit);

        AuditResponse response = auditService.closeAudit(1L, request);

        assertNotNull(response);
        assertEquals(StatusBasic.Inactive, response.getStatus());
        assertEquals("Audit completed and closed", response.getFindings());

        verify(auditRepository, times(1)).findById(1L);
        verify(auditRepository, times(1)).save(any(Audit.class));
    }

    @Test
    void shouldCloseAuditSuccessfullyWithoutChangingFindingsWhenBlank() {
        CloseAuditRequest request = CloseAuditRequest.builder()
                .findings("   ")
                .build();

        Audit existingAudit = Audit.builder()
                .id(2L)
                .officerId(401L)
                .scope("Special Audit")
                .findings("Old findings")
                .status(StatusBasic.Active)
                .createdAt(Instant.now())
                .build();

        Audit savedAudit = Audit.builder()
                .id(2L)
                .officerId(401L)
                .scope("Special Audit")
                .findings("Old findings")
                .status(StatusBasic.Inactive)
                .createdAt(existingAudit.getCreatedAt())
                .build();

        when(auditRepository.findById(2L)).thenReturn(Optional.of(existingAudit));
        when(auditRepository.save(any(Audit.class))).thenReturn(savedAudit);

        AuditResponse response = auditService.closeAudit(2L, request);

        assertNotNull(response);
        assertEquals("Old findings", response.getFindings());
        assertEquals(StatusBasic.Inactive, response.getStatus());

        verify(auditRepository, times(1)).findById(2L);
        verify(auditRepository, times(1)).save(any(Audit.class));
    }

    @Test
    void shouldThrowExceptionWhenClosingNonExistingAudit() {
        CloseAuditRequest request = CloseAuditRequest.builder()
                .findings("Closing remarks")
                .build();

        when(auditRepository.findById(100L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(
                NoSuchElementException.class,
                () -> auditService.closeAudit(100L, request)
        );

        assertEquals("Audit not found", exception.getMessage());

        verify(auditRepository, times(1)).findById(100L);
        verify(auditRepository, never()).save(any(Audit.class));
    }
}