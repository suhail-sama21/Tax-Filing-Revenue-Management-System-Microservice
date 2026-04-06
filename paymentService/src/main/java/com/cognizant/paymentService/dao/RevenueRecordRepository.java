package com.cognizant.paymentService.dao;

import com.cognizant.paymentService.entity.RevenueRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;

public interface RevenueRecordRepository extends JpaRepository<RevenueRecord, Long> {
    @Query("SELECT SUM(r.amount) FROM RevenueRecord r WHERE r.status = 'Completed'")
    BigDecimal sumCollectedRevenue();
}