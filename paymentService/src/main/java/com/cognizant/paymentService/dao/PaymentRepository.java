package com.cognizant.paymentService.dao;

import com.cognizant.paymentService.entity.Payment;

import com.cognizant.paymentService.entity.entityEnum.PaymentMethod;
import com.cognizant.paymentService.entity.entityEnum.StatusBasic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByTaxpayerId(Long taxpayerId);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.status = 'Pending' OR p.status = 'Failed'")
    BigDecimal sumOutstandingPayments();

    List<Payment> findByMethod(PaymentMethod method);
}