package com.cognizant.paymentService.dao;

import com.cognizant.paymentService.entity.Payment;
import com.cognizant.paymentService.entity.entityEnum.PaymentMethod;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // Fulfills TAXFR-11: Get payments for a specific taxpayer
    List<Payment> findByFiling_Taxpayer_Id(Long taxpayerId);
    long countByStatus(StatusBasic status);
    long countByStatusAndMethod(StatusBasic status, PaymentMethod method);
    long countByMethod(PaymentMethod method);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'Pending'")
    BigDecimal sumOutstandingPayments();
}