package com.cognizant.taxFilingService.service;

import com.cognizant.taxFilingService.client.TaxpayerServiceClient;
import com.cognizant.taxFilingService.dao.TaxFilingRepository;
import com.cognizant.taxFilingService.dto.requestdto.TaxFilingRequestDTO;
import com.cognizant.taxFilingService.dto.responsedto.*;
import com.cognizant.taxFilingService.dto.responsedto.*;
import com.cognizant.taxFilingService.entity.TaxFiling;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxFilingService {

    private final TaxFilingRepository taxFilingRepository;
    private final TaxpayerServiceClient taxpayerServiceClient; // Feign Client to verify Taxpayer

    @Transactional
    public TaxFilingResponseDTO submitFiling(TaxFilingRequestDTO dto) {
        log.info("Submitting filing for Taxpayer ID: {}", dto.getTaxpayerId());

        // Check if taxpayer exists via Feign
        try {
            taxpayerServiceClient.verifyTaxpayerExists(dto.getTaxpayerId());
        } catch (Exception e) {
            throw new RuntimeException("Cannot submit filing: Taxpayer profile does not exist for ID " + dto.getTaxpayerId());
        }

        TaxFiling filing = TaxFiling.builder()
                .taxpayerId(dto.getTaxpayerId())
                .period(dto.getPeriod())
                .amountDeclared(dto.getAmountDeclared())
                .status("Pending")
                .build();

        TaxFiling savedFiling = taxFilingRepository.save(filing);
        return mapToDTO(savedFiling);
    }

    public List<TaxFilingResponseDTO> getFilingHistory(Long taxpayerId) {
        log.info("Fetching filing history for Taxpayer ID: {}", taxpayerId);
        return taxFilingRepository.findByTaxpayerId(taxpayerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaxFilingResponseDTO updateFilingStatus(Long filingId, String status, Long officerId) {
        log.info("Updating filing {} to status {}", filingId, status);

        TaxFiling filing = taxFilingRepository.findById(filingId)
                .orElseThrow(() -> new RuntimeException("Filing not found"));

        filing.setStatus(status);
        if (officerId != null) {
            filing.setOfficerId(officerId);
        }

        TaxFiling updated = taxFilingRepository.save(filing);
        return mapToDTO(updated);
    }

    private TaxFilingResponseDTO mapToDTO(TaxFiling filing) {
        return TaxFilingResponseDTO.builder()
                .id(filing.getId())
                .taxpayerId(filing.getTaxpayerId())
                .period(filing.getPeriod())
                .amountDeclared(filing.getAmountDeclared())
                .status(filing.getStatus())
                .officerId(filing.getOfficerId())
                .submittedDate(filing.getSubmittedDate())
                .build();
    }
}