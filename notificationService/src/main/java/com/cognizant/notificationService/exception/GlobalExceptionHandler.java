package com.cognizant.notificationService.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
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

    // 1. Handles our custom "Not Found" exceptions (Returns 404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 2. Fallback for any other unexpected errors (Returns 500)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {

        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred: " + ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}