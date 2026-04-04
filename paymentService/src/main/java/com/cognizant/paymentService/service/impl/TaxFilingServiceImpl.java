package com.cognizant.paymentService.service.impl;

import com.cognizant.taxease.dto.requestdto.TaxFilingRequestDTO;
import com.cognizant.taxease.dto.responsedto.TaxFilingResponseDTO;
import com.cognizant.taxease.entity.TaxFiling;
import com.cognizant.taxease.entity.Taxpayer;
import com.cognizant.taxease.entity.User;
import com.cognizant.taxease.entity.entityEnum.StatusBasic;
import com.cognizant.taxease.dao.TaxFilingRepository;
import com.cognizant.taxease.dao.TaxpayerRepository;
import com.cognizant.taxease.dao.UserRepository;
import com.cognizant.taxease.entity.entityEnum.UserRole;
import com.cognizant.taxease.service.AuditLogService;
import com.cognizant.taxease.service.TaxFilingService;
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