package com.cognizant.auditservice.repository;

import com.cognizant.auditservice.entity.Audit;
import com.cognizant.auditservice.entityenum.StatusBasic;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditRepository extends JpaRepository<Audit, Long> {

    long countByStatus(StatusBasic status);

}