package com.cognizant.taxease.reportingService.exception;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Handle Missing Data (Instead of custom ResourceNotFoundException, we catch Java's built-in exception)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("Bad Request / Data Not Found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST);
    }

    // 2. Handle Feign Exceptions (Crucial for Microservices - Catches errors from Payment/Compliance services)
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignException(FeignException ex, WebRequest request) {
        log.error("Feign Client Exception: {}", ex.getMessage());

        String message = "Error communicating with downstream microservice: " + ex.getMessage();
        int status = ex.status() >= 400 ? ex.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

        return buildErrorResponse(message, request.getDescription(false), HttpStatus.valueOf(status));
    }

    // 3. Handle Global (All Other) Exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unexpected Error: ", ex);
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Helper method to build the JSON response without needing an ErrorDetails.java class
    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, String details, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("details", details);

        return new ResponseEntity<>(errorResponse, status);
    }
}