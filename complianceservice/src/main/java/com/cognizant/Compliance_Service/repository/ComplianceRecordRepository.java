package com.cognizant.Compliance_Service.repository;

import com.cognizant.Compliance_Service.entity.ComplianceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {

    List<ComplianceRecord> findByTaxpayerId(Long taxpayerId);

    List<ComplianceRecord> findByResultIgnoreCase(String result);

    long countByResultIgnoreCase(String result);
}