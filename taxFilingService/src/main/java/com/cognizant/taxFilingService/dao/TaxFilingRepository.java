package com.cognizant.taxFilingService.dao;

import com.cognizant.taxFilingService.entity.TaxFiling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaxFilingRepository extends JpaRepository<TaxFiling, Long> {
    // Spring Data JPA automatically traverses the 'taxpayer' entity to find its 'id'
    List<TaxFiling> findByTaxpayerId(Long taxpayerId);
}