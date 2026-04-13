package com.cognizant.taxFilingService.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeExceptions(RuntimeException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("error", ex.getMessage());
        response.put("status", HttpStatus.NOT_FOUND.value());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("message", "An unexpected error occurred");
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(feign.FeignException.NotFound.class)
    public ResponseEntity<Map<String, Object>> handleFeignNotFound(feign.FeignException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", "Taxpayer Service reported: User profile not found.");
        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
}