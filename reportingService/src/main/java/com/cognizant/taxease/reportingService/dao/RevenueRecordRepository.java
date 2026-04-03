package com.cognizant.taxease.reportingService.dao;

import com.cognizant.taxease.reportingService.entity.RevenueRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Repository
public interface RevenueRecordRepository extends JpaRepository<RevenueRecord, Long> {
    @Query("SELECT SUM(r.amount) FROM RevenueRecord r WHERE r.status = 'Completed'")
    BigDecimal sumCollectedRevenue();

    List<RevenueRecord> findByDateBetween(Instant startDate, Instant endDate);
}