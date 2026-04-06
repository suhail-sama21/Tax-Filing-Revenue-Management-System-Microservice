package com.cognizant.taxpayerService.dao;

import com.cognizant.taxpayerService.entity.Taxpayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaxpayerRepository extends JpaRepository<Taxpayer, Long> {

    // MICROSERVICE CHANGE: Changed from findByUser to findByUserEmail
    Optional<Taxpayer> findByUserId(Long ID);
    boolean existsByTaxpayerIdNumber(String taxpayerIdNumber);

}