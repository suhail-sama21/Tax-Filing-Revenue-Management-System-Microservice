package com.cognizant.taxease.reportingService.dao;

import com.cognizant.taxease.reportingService.entity.Payment;
import com.cognizant.taxease.reportingService.entity.entityEnum.PaymentMethod;
import com.cognizant.taxease.reportingService.entity.entityEnum.StatusBasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByFiling_Taxpayer_Id(Long taxpayerId);
    long countByStatus(StatusBasic status);
    long countByStatusAndMethod(StatusBasic status, PaymentMethod method);
    long countByMethod(PaymentMethod method);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'Pending'")
    BigDecimal sumOutstandingPayments();
}