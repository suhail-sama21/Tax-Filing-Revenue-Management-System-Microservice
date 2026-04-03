package com.cognizant.paymentService.service.impl;

import com.cognizant.taxease.dao.UserRepository;
import com.cognizant.taxease.dto.requestdto.FilingDocumentRequestDTO;
import com.cognizant.taxease.dto.responsedto.FilingDocumentResponseDTO;
import com.cognizant.taxease.entity.FilingDocument;
import com.cognizant.taxease.entity.TaxFiling;
import com.cognizant.taxease.dao.FilingDocumentRepository;
import com.cognizant.taxease.dao.TaxFilingRepository;
import com.cognizant.taxease.entity.Taxpayer;
import com.cognizant.taxease.entity.User;
import com.cognizant.taxease.entity.entityEnum.UserRole;
import com.cognizant.taxease.service.AuditLogService;
import com.cognizant.taxease.service.FilingDocumentService;
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
public class FilingDocumentServiceImpl implements FilingDocumentService {

    private final FilingDocumentRepository documentRepository;
    private final TaxFilingRepository taxFilingRepository;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public FilingDocumentResponseDTO addDocument(FilingDocumentRequestDTO dto) {
        TaxFiling filing = taxFilingRepository.findById(dto.getFilingId())
                .orElseThrow(() -> new RuntimeException("Filing not found"));

        FilingDocument document = FilingDocument.builder()
                .filing(filing)
                .fileUrl(dto.getFileUrl())
                .build();

        FilingDocument savedDoc = documentRepository.save(document);

        auditLogService.record("DOCUMENT_UPLOAD", "filing_documents/" + savedDoc.getId());
        return FilingDocumentResponseDTO.builder()
                .id(savedDoc.getId())
                .filingId(savedDoc.getFiling().getId())
                .fileUrl(savedDoc.getFileUrl())
                .uploadedDate(savedDoc.getUploadedDate())
                .build();
    }

    @Override
    @Transactional
    public List<FilingDocumentResponseDTO> getDocumentsByFiling(Long filingId) {
        auditLogService.record("DOCUMENT_LIST_VIEW", "filings/" + filingId + "/documents");
        UserDetails userDetails=(UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user=userRepository.findByEmail(userDetails.getUsername()).orElseThrow();
        if(user.getRole().equals(UserRole.TAXPAYER)) {
            Taxpayer taxpayer = user.getTaxpayer();
            List<TaxFiling> taxfilings = taxpayer.getTaxFilings();
            long c = taxfilings.stream().filter(filing -> filing.getId().equals(filingId)).count();
            if (c == 0) {
                throw new AccessDeniedException("Access Denied");
            }
        }
        return documentRepository.findByFilingId(filingId).stream()
                .map(doc -> FilingDocumentResponseDTO.builder()
                        .id(doc.getId())
                        .filingId(doc.getFiling().getId())
                        .fileUrl(doc.getFileUrl())
                        .uploadedDate(doc.getUploadedDate())
                        .build())
                .collect(Collectors.toList());
    }
}