package com.cognizant.taxFilingService.service.impl;

import com.cognizant.taxFilingService.dao.TaxFilingRepository;
import com.cognizant.taxease.taxFilingService.dao.TaxpayerRepository;
import com.cognizant.taxease.taxFilingService.dao.UserRepository;
import com.cognizant.taxFilingService.dto.requestdto.TaxFilingRequestDTO;
import com.cognizant.taxFilingService.dto.responsedto.TaxFilingResponseDTO;
import com.cognizant.taxFilingService.entity.TaxFiling;
import com.cognizant.taxFilingService.entity.Taxpayer;
import com.cognizant.taxFilingService.entity.User;
import com.cognizant.taxFilingService.entity.entityEnum.StatusBasic;
import com.cognizant.taxFilingService.entity.entityEnum.UserRole;
import com.cognizant.taxease.taxFilingService.service.AuditLogService;
import com.cognizant.taxFilingService.service.TaxFilingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaxFilingServiceImpl implements TaxFilingService {

    private final TaxFilingRepository taxFilingRepository;
    private final TaxpayerRepository taxpayerRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long filingId) {

        return taxFilingRepository.existsById(filingId);
    }

    @Override
    @Transactional
    public TaxFilingResponseDTO submitFiling(TaxFilingRequestDTO dto) {
        Taxpayer taxpayer = taxpayerRepository.findById(dto.getTaxpayerId())
                .orElseThrow(() -> new RuntimeException("Taxpayer not found"));

        TaxFiling filing = TaxFiling.builder()
                .taxpayer(taxpayer)
                .period(dto.getPeriod())
                .amountDeclared(dto.getAmountDeclared())
                .status(StatusBasic.Pending)
                .build();

        TaxFiling savedFiling = taxFilingRepository.save(filing);
        auditLogService.record("FILING_SUBMIT", "tax_filings/" + savedFiling.getId());
        return mapToDTO(savedFiling);
    }

    @Override
    @Transactional
    public List<TaxFilingResponseDTO> getFilingHistory(Long taxpayerId) {
        UserDetails userDetails=(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user=userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        if(user.getRole().equals(UserRole.TAXPAYER)) {
            Taxpayer taxpayer = user.getTaxpayer();

            if (!taxpayer.getId().equals(taxpayerId)) {
                throw new AccessDeniedException("Access Denied");
            }
        }
        auditLogService.record("FILING_HISTORY_VIEW", "taxpayer/" + taxpayerId + "/filings");
        return taxFilingRepository.findByTaxpayerId(taxpayerId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TaxFilingResponseDTO updateFilingStatus(Long filingId, String newStatus, Long officerId) {
        TaxFiling filing = taxFilingRepository.findById(filingId)
                .orElseThrow(() -> new RuntimeException("Filing not found"));

        filing.setStatus(StatusBasic.valueOf(newStatus));

        if (officerId != null) {
            User officer = userRepository.findById(officerId)
                    .orElseThrow(() -> new RuntimeException("Officer not found"));
            filing.setOfficer(officer);
        }
        TaxFiling updated = taxFilingRepository.save(filing);
        auditLogService.record("FILING_STATUS_UPDATE", "tax_filings/" + updated.getId());
        return mapToDTO(updated);
    }

    private TaxFilingResponseDTO mapToDTO(TaxFiling filing) {
        return TaxFilingResponseDTO.builder()
                .id(filing.getId())
                .taxpayerId(filing.getTaxpayer().getId())
                .period(filing.getPeriod())
                .amountDeclared(filing.getAmountDeclared())
                .status(filing.getStatus().name())
                .submittedDate(filing.getSubmittedDate())
                .build();
    }
}