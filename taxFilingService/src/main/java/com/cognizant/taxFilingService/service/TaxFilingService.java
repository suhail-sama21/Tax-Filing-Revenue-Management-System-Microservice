package com.cognizant.taxFilingService.service;

import com.cognizant.taxFilingService.dto.requestdto.TaxFilingRequestDTO;
import com.cognizant.taxFilingService.dto.responsedto.TaxFilingResponseDTO;
import java.util.List;

public interface TaxFilingService {
    TaxFilingResponseDTO submitFiling(TaxFilingRequestDTO dto);
    List<TaxFilingResponseDTO> getFilingHistory(Long taxpayerId);
    TaxFilingResponseDTO updateFilingStatus(Long filingId, String status, Long officerId);
    TaxFilingResponseDTO getFilingById(Long filingId);
}