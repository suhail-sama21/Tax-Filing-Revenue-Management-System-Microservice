package com.cognizant.taxFilingService.controller;

import com.cognizant.taxFilingService.dto.responsedto.*;
import com.cognizant.taxFilingService.dto.requestdto.*;
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
@Validated
@Slf4j
public class TaxFilingController {

    private final TaxFilingService taxFilingService;

    @PostMapping("/submit")
    public ResponseEntity<TaxFilingResponseDTO> submitFiling(@Valid @RequestBody TaxFilingRequestDTO dto) {
        log.info("START: New tax filing submission for period: {}", dto.getPeriod());
        TaxFilingResponseDTO response = taxFilingService.submitFiling(dto);
        log.info("END: Filing submission successful | ID: {}", response.getId());
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/taxpayer/{taxpayerId}")
    public ResponseEntity<List<TaxFilingResponseDTO>> getHistory(@PathVariable Long taxpayerId) {
        log.info("START: Fetching history for taxpayer ID: {}", taxpayerId);
        List<TaxFilingResponseDTO> response = taxFilingService.getFilingHistory(taxpayerId);
        log.info("END: Retrieved {} filings", response.size());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{filingId}/status")
    public ResponseEntity<TaxFilingResponseDTO> updateStatus(
            @PathVariable Long filingId,
            @NotBlank(message = "Status is required") @RequestParam String status,
            @RequestParam(required = false) Long officerId) {
        log.info("START: Updating status for Filing ID: {} to {}", filingId, status);
        TaxFilingResponseDTO response = taxFilingService.updateFilingStatus(filingId, status, officerId);
        log.info("END: Status update successful");
        return ResponseEntity.ok(response);
    }
}