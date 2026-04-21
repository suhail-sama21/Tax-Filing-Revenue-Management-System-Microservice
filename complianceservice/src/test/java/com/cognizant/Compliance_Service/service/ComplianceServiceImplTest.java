package com.cognizant.Compliance_Service.service;

import com.cognizant.Compliance_Service.client.PaymentClient;
import com.cognizant.Compliance_Service.client.TaxFilingClient;
import com.cognizant.Compliance_Service.client.TaxpayerClient;
import com.cognizant.Compliance_Service.dto.ComplianceResponse;
import com.cognizant.Compliance_Service.dto.CreateComplianceRequest;
import com.cognizant.Compliance_Service.dto.UpdateComplianceRequest;
import com.cognizant.Compliance_Service.entity.ComplianceRecord;
import com.cognizant.Compliance_Service.entity.entityenum.ComplianceType;
import com.cognizant.Compliance_Service.repository.ComplianceRecordRepository;
import com.cognizant.Compliance_Service.service.impl.ComplianceServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ComplianceServiceImpl
 */
@ExtendWith(MockitoExtension.class)
class ComplianceServiceImplTest {

    @Mock
    private ComplianceRecordRepository complianceRecordRepository;

    @Mock
    private TaxpayerClient taxpayerClient;

    @Mock
    private TaxFilingClient taxFilingClient;

    @Mock
    private PaymentClient paymentClient;

    @InjectMocks
    private ComplianceServiceImpl complianceService;

    @Test
    void shouldReturnAllCompliance() {
        ComplianceRecord record1 = ComplianceRecord.builder()
                .id(1L)
                .taxpayerId(101L)
                .filingId(201L)
                .paymentId(null)
                .type(ComplianceType.Filing)
                .result("Compliant")
                .notes("Checked")
                .date(LocalDate.now())
                .createdAt(Instant.now())
                .build();

        ComplianceRecord record2 = ComplianceRecord.builder()
                .id(2L)
                .taxpayerId(102L)
                .filingId(null)
                .paymentId(301L)
                .type(ComplianceType.Payment)
                .result("Non-Compliant")
                .notes("Payment mismatch")
                .date(LocalDate.now())
                .createdAt(Instant.now())
                .build();

        when(complianceRecordRepository.findAll()).thenReturn(List.of(record1, record2));

        List<ComplianceResponse> responses = complianceService.getAllCompliance();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(1L, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());

        verify(complianceRecordRepository, times(1)).findAll();
    }

    @Test
    void shouldCreateComplianceSuccessfullyForFiling() {
        CreateComplianceRequest request = new CreateComplianceRequest();
        request.setTaxpayerId(1L);
        request.setFilingId(10L);
        request.setType(ComplianceType.Filing);
        request.setResult("Compliant");
        request.setNotes("All good");

        when(taxpayerClient.verifyTaxpayerExists(1L)).thenReturn(true);
        when(taxFilingClient.verifyFilingExists(10L)).thenReturn(true);

        when(complianceRecordRepository.save(any(ComplianceRecord.class)))
                .thenAnswer(invocation -> {
                    ComplianceRecord saved = invocation.getArgument(0);
                    saved.setId(100L);
                    saved.setCreatedAt(Instant.now());
                    return saved;
                });

        ComplianceResponse response = complianceService.createCompliance(request);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(1L, response.getTaxpayerId());
        assertEquals(10L, response.getFilingId());
        assertNull(response.getPaymentId());
        assertEquals(ComplianceType.Filing, response.getType());
        assertEquals("Compliant", response.getResult());
        assertEquals("All good", response.getNotes());

        verify(taxpayerClient).verifyTaxpayerExists(1L);
        verify(taxFilingClient).verifyFilingExists(10L);
        verify(paymentClient, never()).verifyPaymentExists(any());
        verify(complianceRecordRepository).save(any(ComplianceRecord.class));
    }

    @Test
    void shouldCreateComplianceSuccessfullyForPayment() {
        CreateComplianceRequest request = new CreateComplianceRequest();
        request.setTaxpayerId(1L);
        request.setPaymentId(20L);
        request.setType(ComplianceType.Payment);
        request.setResult("Non-Compliant");
        request.setNotes("Payment issue");

        when(taxpayerClient.verifyTaxpayerExists(1L)).thenReturn(new Object());
        when(paymentClient.verifyPaymentExists(any())).thenReturn(true);

        when(complianceRecordRepository.save(any(ComplianceRecord.class)))
                .thenAnswer(invocation -> {
                    ComplianceRecord saved = invocation.getArgument(0);
                    saved.setId(101L);
                    saved.setCreatedAt(Instant.now());
                    return saved;
                });

        ComplianceResponse response = complianceService.createCompliance(request);

        assertNotNull(response);
        assertEquals(101L, response.getId());
        assertEquals(1L, response.getTaxpayerId());
        assertNull(response.getFilingId());
        assertEquals(20L, response.getPaymentId());
        assertEquals(ComplianceType.Payment, response.getType());
        assertEquals("Non-Compliant", response.getResult());
        assertEquals("Payment issue", response.getNotes());

        verify(taxpayerClient).verifyTaxpayerExists(1L);
        verify(paymentClient).verifyPaymentExists(20L);
        verify(taxFilingClient, never()).verifyFilingExists(any());
        verify(complianceRecordRepository).save(any(ComplianceRecord.class));
    }

    @Test
    void shouldThrowExceptionWhenTaxpayerNotFound() {
        CreateComplianceRequest request = new CreateComplianceRequest();
        request.setTaxpayerId(1L);
        request.setFilingId(10L);
        request.setType(ComplianceType.Filing);

        doThrow(new RuntimeException("Taxpayer service error"))
                .when(taxpayerClient).verifyTaxpayerExists(1L);

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> complianceService.createCompliance(request)
        );

        assertEquals("Taxpayer not found in Taxpayer Service", ex.getMessage());

        verify(taxpayerClient).verifyTaxpayerExists(1L);
        verify(complianceRecordRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenFilingIdMissingForFilingType() {
        CreateComplianceRequest request = new CreateComplianceRequest();
        request.setTaxpayerId(1L);
        request.setType(ComplianceType.Filing);
        request.setResult("Compliant");

        when(taxpayerClient.verifyTaxpayerExists(1L)).thenReturn(new Object());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> complianceService.createCompliance(request)
        );

        assertEquals("filingId is required when type is Filing", ex.getMessage());

        verify(taxpayerClient).verifyTaxpayerExists(1L);
        verify(taxFilingClient, never()).verifyFilingExists(any());
        verify(complianceRecordRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPaymentIdMissingForPaymentType() {
        CreateComplianceRequest request = new CreateComplianceRequest();
        request.setTaxpayerId(1L);
        request.setType(ComplianceType.Payment);
        request.setResult("Compliant");

        when(taxpayerClient.verifyTaxpayerExists(1L)).thenReturn(new Object());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> complianceService.createCompliance(request)
        );

        assertEquals("paymentId is required when type is Payment", ex.getMessage());

        verify(taxpayerClient).verifyTaxpayerExists(1L);
        verify(paymentClient, never()).verifyPaymentExists(any());
        verify(complianceRecordRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenFilingNotFoundInTaxFilingService() {
        CreateComplianceRequest request = new CreateComplianceRequest();
        request.setTaxpayerId(1L);
        request.setFilingId(10L);
        request.setType(ComplianceType.Filing);
        request.setResult("Compliant");

        when(taxpayerClient.verifyTaxpayerExists(1L)).thenReturn(new Object());

        doThrow(new RuntimeException("Filing service error"))
                .when(taxFilingClient).verifyFilingExists(10L);

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> complianceService.createCompliance(request)
        );

        assertEquals("Filing not found in Tax Filing Service", ex.getMessage());

        verify(taxpayerClient).verifyTaxpayerExists(1L);
        verify(taxFilingClient).verifyFilingExists(10L);
        verify(complianceRecordRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFoundInPaymentService() {
        CreateComplianceRequest request = new CreateComplianceRequest();
        request.setTaxpayerId(1L);
        request.setPaymentId(20L);
        request.setType(ComplianceType.Payment);
        request.setResult("Non-Compliant");

        when(taxpayerClient.verifyTaxpayerExists(1L)).thenReturn(new Object());

        doThrow(new RuntimeException("Payment service error"))
                .when(paymentClient).verifyPaymentExists(20L);

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> complianceService.createCompliance(request)
        );

        assertEquals("Payment not found in Payment Service", ex.getMessage());

        verify(taxpayerClient).verifyTaxpayerExists(1L);
        verify(paymentClient).verifyPaymentExists(20L);
        verify(complianceRecordRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUnsupportedComplianceType() {
        CreateComplianceRequest request = new CreateComplianceRequest();
        request.setTaxpayerId(1L);
        request.setResult("Compliant");
        request.setNotes("Invalid type");

        when(taxpayerClient.verifyTaxpayerExists(1L)).thenReturn(new Object());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> complianceService.createCompliance(request)
        );

        assertTrue(ex.getMessage().contains("Unsupported compliance type"));

        verify(taxpayerClient).verifyTaxpayerExists(1L);
        verify(complianceRecordRepository, never()).save(any());
    }

    @Test
    void shouldReturnComplianceById() {
        Long id = 1L;

        ComplianceRecord record = ComplianceRecord.builder()
                .id(id)
                .taxpayerId(1L)
                .filingId(10L)
                .paymentId(null)
                .type(ComplianceType.Filing)
                .result("Compliant")
                .notes("Checked")
                .date(LocalDate.now())
                .createdAt(Instant.now())
                .build();

        when(complianceRecordRepository.findById(id)).thenReturn(Optional.of(record));

        ComplianceResponse response = complianceService.getComplianceById(id);

        assertNotNull(response);
        assertEquals(id, response.getId());
        assertEquals(1L, response.getTaxpayerId());
        assertEquals("Compliant", response.getResult());

        verify(complianceRecordRepository).findById(id);
    }

    @Test
    void shouldThrowExceptionWhenComplianceNotFoundById() {
        when(complianceRecordRepository.findById(1L)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> complianceService.getComplianceById(1L)
        );

        assertEquals("Compliance not found", ex.getMessage());

        verify(complianceRecordRepository).findById(1L);
    }

    @Test
    void shouldUpdateComplianceSuccessfully() {
        Long id = 1L;

        ComplianceRecord record = ComplianceRecord.builder()
                .id(id)
                .taxpayerId(1L)
                .filingId(10L)
                .type(ComplianceType.Filing)
                .result("Compliant")
                .notes("Old notes")
                .date(LocalDate.now())
                .createdAt(Instant.now())
                .build();

        UpdateComplianceRequest request = new UpdateComplianceRequest();
        request.setResult("Compliant");
        request.setNotes("Updated notes");

        when(complianceRecordRepository.findById(id)).thenReturn(Optional.of(record));
        when(complianceRecordRepository.save(any(ComplianceRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ComplianceResponse response = complianceService.updateCompliance(id, request);

        assertNotNull(response);
        assertEquals("Compliant", response.getResult());
        assertEquals("Updated notes", response.getNotes());

        verify(complianceRecordRepository).findById(id);
        verify(complianceRecordRepository).save(record);
    }

    @Test
    void shouldUpdateComplianceToNonCompliantSuccessfully() {
        Long id = 1L;

        ComplianceRecord record = ComplianceRecord.builder()
                .id(id)
                .taxpayerId(1L)
                .paymentId(20L)
                .type(ComplianceType.Payment)
                .result("Compliant")
                .notes("Old notes")
                .date(LocalDate.now())
                .createdAt(Instant.now())
                .build();

        UpdateComplianceRequest request = new UpdateComplianceRequest();
        request.setResult("Non-Compliant");
        request.setNotes("Mismatch found");

        when(complianceRecordRepository.findById(id)).thenReturn(Optional.of(record));
        when(complianceRecordRepository.save(any(ComplianceRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ComplianceResponse response = complianceService.updateCompliance(id, request);

        assertNotNull(response);
        assertEquals("Non-Compliant", response.getResult());
        assertEquals("Mismatch found", response.getNotes());

        verify(complianceRecordRepository).findById(id);
        verify(complianceRecordRepository).save(record);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingCompliance() {
        Long id = 99L;

        UpdateComplianceRequest request = new UpdateComplianceRequest();
        request.setResult("Compliant");
        request.setNotes("Any note");

        when(complianceRecordRepository.findById(id)).thenReturn(Optional.empty());

        NoSuchElementException ex = assertThrows(
                NoSuchElementException.class,
                () -> complianceService.updateCompliance(id, request)
        );

        assertEquals("Compliance not found", ex.getMessage());

        verify(complianceRecordRepository).findById(id);
        verify(complianceRecordRepository, never()).save(any());
    }

    @Test
    void shouldReturnComplianceByTaxpayerId() {
        ComplianceRecord record = ComplianceRecord.builder()
                .id(1L)
                .taxpayerId(1L)
                .filingId(10L)
                .type(ComplianceType.Filing)
                .result("Compliant")
                .date(LocalDate.now())
                .createdAt(Instant.now())
                .build();

        when(complianceRecordRepository.findByTaxpayerId(1L)).thenReturn(List.of(record));

        List<ComplianceResponse> responses = complianceService.getComplianceByTaxpayerId(1L);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(1L, responses.get(0).getTaxpayerId());

        verify(complianceRecordRepository).findByTaxpayerId(1L);
    }

    @Test
    void shouldReturnComplianceByResultIgnoreCase() {
        ComplianceRecord record = ComplianceRecord.builder()
                .id(1L)
                .taxpayerId(1L)
                .paymentId(20L)
                .type(ComplianceType.Payment)
                .result("Compliant")
                .date(LocalDate.now())
                .createdAt(Instant.now())
                .build();

        when(complianceRecordRepository.findByResultIgnoreCase("Compliant"))
                .thenReturn(List.of(record));

        List<ComplianceResponse> responses = complianceService.getByResult("Compliant");

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("Compliant", responses.get(0).getResult());

        verify(complianceRecordRepository).findByResultIgnoreCase("Compliant");
    }
}