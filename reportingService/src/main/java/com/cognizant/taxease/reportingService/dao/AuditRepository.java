package com.cognizant.taxease.reportingService.dao;

import com.cognizant.taxease.reportingService.entity.Audit;
import com.cognizant.taxease.reportingService.entity.entityEnum.StatusBasic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AuditRepository extends JpaRepository<Audit, Long> {
    long countByStatus(StatusBasic status);

    List<Audit> findByStatus(StatusBasic status);

    Page<Audit> findByStatus(StatusBasic status, Pageable pageable);
}