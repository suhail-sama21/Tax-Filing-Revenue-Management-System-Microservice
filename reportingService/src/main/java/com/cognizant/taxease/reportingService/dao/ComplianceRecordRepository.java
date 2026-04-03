package com.cognizant.taxease.reportingService.dao;

import com.cognizant.taxease.reportingService.entity.ComplianceRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {
    List<ComplianceRecord> findByTaxpayer_Id(Long taxpayerId);
    List<ComplianceRecord> findByResultIgnoreCase(String result);
    long countByResultIgnoreCase(String result);

    // NEW: Fetch compliance records between two dates
    List<ComplianceRecord> findByDateBetween(LocalDate startDate, LocalDate endDate);
}