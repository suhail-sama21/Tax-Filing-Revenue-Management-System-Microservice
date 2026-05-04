package com.cognizant.taxFilingService.controller;

import com.cognizant.taxFilingService.dto.responsedto.*;
import com.cognizant.taxFilingService.dto.requestdto.*;
import com.cognizant.taxFilingService.service.FilingDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Validated
@Slf4j
public class FilingDocumentController {

    private final FilingDocumentService documentService;

    @PostMapping("/upload")
    public ResponseEntity<FilingDocumentResponseDTO> uploadDocument(
            @Valid @RequestBody FilingDocumentRequestDTO dto) {
        log.info("START: Uploading document for filing ID: {}", dto.getFilingId());
        FilingDocumentResponseDTO response = documentService.addDocument(dto);
        log.info("END: Document upload successful | ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/filing/{filingId}")
    public ResponseEntity<List<FilingDocumentResponseDTO>> getDocuments(@PathVariable Long filingId) {
        log.info("START: Fetching documents by filing ID: {}", filingId);
        List<FilingDocumentResponseDTO> response = documentService.getDocumentsByFiling(filingId);
        log.info("END: Successfully fetched documents");
        return ResponseEntity.ok(response);
    }
}