package com.cognizant.taxFilingService.service;

import com.cognizant.taxFilingService.dao.FilingDocumentRepository;
import com.cognizant.taxFilingService.dao.TaxFilingRepository;
import com.cognizant.taxFilingService.dto.requestdto.*;
import com.cognizant.taxFilingService.dto.responsedto.*;
import com.cognizant.taxFilingService.entity.FilingDocument;
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
public class FilingDocumentService {

    private final FilingDocumentRepository documentRepository;
    private final TaxFilingRepository taxFilingRepository;

    @Transactional
    public FilingDocumentResponseDTO addDocument(FilingDocumentRequestDTO dto) {
        log.info("Adding document to filing ID: {}", dto.getFilingId());

        TaxFiling filing = taxFilingRepository.findById(dto.getFilingId())
                .orElseThrow(() -> new RuntimeException("Filing not found"));

        FilingDocument document = FilingDocument.builder()
                .filing(filing)
                .fileUrl(dto.getFileUrl())
                .build();

        FilingDocument savedDoc = documentRepository.save(document);

        return FilingDocumentResponseDTO.builder()
                .id(savedDoc.getId())
                .filingId(savedDoc.getFiling().getId())
                .fileUrl(savedDoc.getFileUrl())
                .uploadedDate(savedDoc.getUploadedDate())
                .build();
    }

    public List<FilingDocumentResponseDTO> getDocumentsByFiling(Long filingId) {
        log.info("Fetching documents for filing ID: {}", filingId);
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