package com.cognizant.taxFilingService.service.impl;

import com.cognizant.taxFilingService.client.TaxpayerServiceClient;
import com.cognizant.taxFilingService.dao.TaxFilingRepository;
import com.cognizant.taxFilingService.dto.requestdto.TaxFilingRequestDTO;
import com.cognizant.taxFilingService.dto.responsedto.TaxFilingResponseDTO;
import com.cognizant.taxFilingService.entity.TaxFiling;
import com.cognizant.taxFilingService.service.TaxFilingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaxFilingServiceImpl implements TaxFilingService {

    private final TaxFilingRepository taxFilingRepository;
    private final TaxpayerServiceClient taxpayerServiceClient;

    @Override
    @Transactional
    public TaxFilingResponseDTO submitFiling(TaxFilingRequestDTO dto) {
        log.info("Submitting filing for Taxpayer ID: {}", dto.getTaxpayerId());
        try {
            taxpayerServiceClient.verifyTaxpayerExists(dto.getTaxpayerId());
        } catch (Exception e) {
            throw new RuntimeException("Taxpayer profile does not exist for ID " + dto.getTaxpayerId());
        }

        TaxFiling filing = TaxFiling.builder()
                .taxpayerId(dto.getTaxpayerId())
                .period(dto.getPeriod())
                .amountDeclared(dto.getAmountDeclared())
                .status("Pending")
                .build();

        return mapToDTO(taxFilingRepository.save(filing));
    }

    @Override
    public List<TaxFilingResponseDTO> getFilingHistory(Long taxpayerId) {
        log.info("Fetching history for Taxpayer ID: {}", taxpayerId);
        return taxFilingRepository.findByTaxpayerId(taxpayerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaxFilingResponseDTO updateFilingStatus(Long filingId, String status, Long officerId) {
        TaxFiling filing = taxFilingRepository.findById(filingId)
                .orElseThrow(() -> new RuntimeException("Filing not found"));
        filing.setStatus(status);
        if (officerId != null) filing.setOfficerId(officerId);
        return mapToDTO(taxFilingRepository.save(filing));
    }

    @Override
    public TaxFilingResponseDTO getFilingById(Long filingId) {
        return taxFilingRepository.findById(filingId)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Filing not found"));
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