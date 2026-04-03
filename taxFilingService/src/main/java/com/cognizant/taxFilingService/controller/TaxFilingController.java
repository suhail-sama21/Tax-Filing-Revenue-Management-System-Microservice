package com.cognizant.taxFilingService.controller;

import com.cognizant.taxFilingService.dto.requestdto.TaxFilingRequestDTO;
import com.cognizant.taxFilingService.dto.responsedto.TaxFilingResponseDTO;
import com.cognizant.taxFilingService.service.TaxFilingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/filings")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Validated
@Slf4j// Enables validation for @RequestParam
public class TaxFilingController {

    private final TaxFilingService taxFilingService;

    @GetMapping("/{filingId}/exists")
    public ResponseEntity<Boolean> checkFilingExists(@PathVariable Long filingId) {
        log.info("Checking existence for Filing ID: {}", filingId);
        return ResponseEntity.ok(taxFilingService.existsById(filingId));
    }

    @PostMapping("/submit")
    public ResponseEntity<TaxFilingResponseDTO> submitFiling(
            @Valid @RequestBody TaxFilingRequestDTO dto) {
        log.info("START: New tax filing submission for year: {}", dto.getPeriod());
        TaxFilingResponseDTO response = taxFilingService.submitFiling(dto);
        log.info("END: Filing submission successful | Filing ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/taxpayer/{taxpayerId}")
    public ResponseEntity<List<TaxFilingResponseDTO>> getHistory(@PathVariable Long taxpayerId) {
        log.info("START: Fetching filing history for taxpayer ID: {}", taxpayerId);
        List<TaxFilingResponseDTO> response = taxFilingService.getFilingHistory(taxpayerId);
        log.info("END: Retrieved {} filings", response.size());
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{filingId}/status")
    public ResponseEntity<TaxFilingResponseDTO> updateStatus(
            @PathVariable Long filingId,
            @NotBlank(message = "Status is required") @RequestParam String status,
            @RequestParam(required = false) Long officerId) {
        log.info("START: Updating status for Filing ID: {} to {} | Officer: {}", filingId, status, officerId);
        TaxFilingResponseDTO response = taxFilingService.updateFilingStatus(filingId, status, officerId);
        log.info("END: Status update successful | Current Status: {}", response.getStatus());
        return ResponseEntity.ok(response);
    }
}