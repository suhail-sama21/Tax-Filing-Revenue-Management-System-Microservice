package com.cognizant.taxease.reportingService.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignException(FeignException e) {
        ObjectMapper mapper = new ObjectMapper(); // Or inject it via @RequiredArgsConstructor

        int status = e.status() == -1 ? 500 : e.status();
        String rawBody = e.contentUTF8();

        // Default message in case parsing fails
        String extractedMessage = rawBody;

        try {
            if (rawBody != null && !rawBody.isEmpty()) {
                // Parse the raw string into a JsonNode tree
                JsonNode root = mapper.readTree(rawBody);

                // Check if the downstream service has a "message" field
                if (root.has("message")) {
                    extractedMessage = root.get("message").asText();
                }
            }
        } catch (Exception parseException) {
            // If it's not JSON, we just keep the rawBody
            extractedMessage = rawBody;
        }

        Map<String, Object> errorDetails = new LinkedHashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("status", status);
        errorDetails.put("error", "Downstream Service Failure");
        errorDetails.put("message", extractedMessage); // This is now the clean string
        errorDetails.put("service_url", e.request().url());

        return ResponseEntity.status(status).body(errorDetails);
    }

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