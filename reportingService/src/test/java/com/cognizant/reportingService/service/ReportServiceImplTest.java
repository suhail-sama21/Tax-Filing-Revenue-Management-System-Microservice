package com.cognizant.reportingService.service;

import com.cognizant.reportingService.client.AuditClient;
import com.cognizant.reportingService.client.ComplianceClient;
import com.cognizant.reportingService.client.PaymentClient;
import com.cognizant.reportingService.dto.*;
import com.cognizant.reportingService.service.impl.ReportServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private AuditClient auditClient;

    @Mock
    private ComplianceClient complianceClient;

    @InjectMocks
    private ReportServiceImpl reportService;

    private AuditDto activeAudit;
    private AuditDto inactiveAudit;
    private ComplianceDto complianceDto;

    @BeforeEach
    void setUp() {
        activeAudit = AuditDto.builder().id(1L).status("Active").build();
        inactiveAudit = AuditDto.builder().id(2L).status("Inactive").build();
        complianceDto = ComplianceDto.builder().id(1L).taxpayerId(101L).result("Non-Compliant").date(LocalDate.now()).build();
    }

    @Test
    void testGetPaymentMetrics_Success() {
        PaymentMetricsResponse expectedResponse = new PaymentMetricsResponse(10, 2, 12);
        when(paymentClient.getPaymentMetrics("UPI")).thenReturn(expectedResponse);

        PaymentMetricsResponse actualResponse = reportService.getPaymentMetrics("UPI");

        assertNotNull(actualResponse);
        assertEquals(10, actualResponse.getSuccessfulTransactions());
        verify(paymentClient, times(1)).getPaymentMetrics("UPI");
    }

    @Test
    void testGetAuditDashboard() {
        when(auditClient.getAllAudits()).thenReturn(Arrays.asList(activeAudit, inactiveAudit));
        when(complianceClient.getByResult("Non-Compliant")).thenReturn(Collections.singletonList(complianceDto));

        AuditDashboardResponse response = reportService.getAuditDashboard();

        assertEquals(2, response.getTotalAudits());
        assertEquals(1, response.getOpenAudits()); // Active
        assertEquals(1, response.getClosedAudits()); // Inactive
        assertEquals(1, response.getNonComplianceFilings());
    }

    @Test
    void testGetCompletedAudits() {
        when(auditClient.getAllAudits()).thenReturn(Arrays.asList(activeAudit, inactiveAudit));

        List<AuditDto> completed = reportService.getCompletedAudits();

        assertEquals(1, completed.size());
        assertEquals("Inactive", completed.get(0).getStatus());
    }

    @Test
    void testGenerateCustomReport_ComplianceOnly() {
        LocalDate start = LocalDate.now().minusDays(5);
        LocalDate end = LocalDate.now();
        when(complianceClient.getAllCompliance()).thenReturn(Collections.singletonList(complianceDto));

        byte[] report = reportService.generateCustomReport(start, end, "ComplianceReport", Collections.singletonList("Compliance"));

        String csvContent = new String(report);
        assertTrue(csvContent.contains("--- COMPLIANCE DATA ---"));
        assertTrue(csvContent.contains("Non-Compliant"));
    }

    @Test
    void testGenerateCustomReport_InvalidDates() {
        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().minusDays(1);

        assertThrows(IllegalArgumentException.class, () ->
                reportService.generateCustomReport(start, end, "Fail", Collections.singletonList("Revenue"))
        );
    }

    @Test
    void testGetPaymentMetrics_Failure() {
        // Simulate a failure in the external payment service
        when(paymentClient.getPaymentMetrics(anyString()))
                .thenThrow(new RuntimeException("Service Down"));

        // Verify that the service wraps the error as expected
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            reportService.getPaymentMetrics("UPI");
        });

        assertTrue(exception.getMessage().contains("Validation Error: Service Down"));
    }

    @Test
    void testGetRevenueDashboard() {
        RevenueDashboardResponse expected = new RevenueDashboardResponse(new BigDecimal("1000"), new BigDecimal("200"));
        when(paymentClient.getRevenueDashboard("monthly", "Individual")).thenReturn(expected);

        RevenueDashboardResponse actual = reportService.getRevenueDashboard("monthly", "Individual");

        assertEquals(expected.getRevenueCollected(), actual.getRevenueCollected());
        verify(paymentClient).getRevenueDashboard("monthly", "Individual");
    }
}