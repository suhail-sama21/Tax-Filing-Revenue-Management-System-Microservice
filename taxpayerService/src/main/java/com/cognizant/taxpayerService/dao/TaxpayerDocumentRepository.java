package com.cognizant.taxpayerService.dao;

import com.cognizant.taxpayerService.entity.Taxpayer;
import com.cognizant.taxpayerService.entity.TaxpayerDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxpayerDocumentRepository extends JpaRepository<TaxpayerDocument, Long> {
    List<TaxpayerDocument> findByTaxpayer(Taxpayer taxpayer);
}