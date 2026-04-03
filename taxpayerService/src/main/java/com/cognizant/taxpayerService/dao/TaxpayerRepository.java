package com.cognizant.taxpayerService.dao;

import com.cognizant.taxpayerService.entity.Taxpayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaxpayerRepository extends JpaRepository<Taxpayer, Long> {
    boolean existsByTaxpayerIdNumber(String taxpayerIdNumber);
    Optional<Taxpayer> findByTaxpayerIdNumber(String taxpayerIdNumber);
    Optional<Taxpayer> findByUser(com.cognizant.taxpayerService.entity.User user);
}