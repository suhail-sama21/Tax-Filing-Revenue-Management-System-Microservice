package com.cognizant.taxFilingService.service;

import com.cognizant.taxFilingService.dto.requestdto.FilingDocumentRequestDTO;
import com.cognizant.taxFilingService.dto.responsedto.FilingDocumentResponseDTO;
import java.util.List;

public interface FilingDocumentService {
    FilingDocumentResponseDTO addDocument(FilingDocumentRequestDTO dto);
    List<FilingDocumentResponseDTO> getDocumentsByFiling(Long filingId);
}