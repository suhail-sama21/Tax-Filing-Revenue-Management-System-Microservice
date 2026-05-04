package com.cognizant.taxFilingService.dao;

import com.cognizant.taxFilingService.entity.FilingDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FilingDocumentRepository extends JpaRepository<FilingDocument, Long> {
    List<FilingDocument> findByFilingId(Long filingId);
}