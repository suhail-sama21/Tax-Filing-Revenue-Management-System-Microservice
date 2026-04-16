package com.cognizant.reportingService.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, String>> handleTokenExpired(ExpiredJwtException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("error", "Token Expired");
        body.put("message", "The provided security token has expired. Please log in again.");

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleAccessDenied(AccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", 403);
        body.put("error", "Forbidden");
        body.put("message", "You do not have permission to access this resource.");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", 401);
        body.put("error", "Unauthorized");
        body.put("message", "Invalid email or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", 404);
        body.put("error", "Not Found");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", 400);
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(FeignException e) {
        int status = e.status() == -1 ? 500 : e.status();
        String rawBody = e.contentUTF8();

        try {
            if (rawBody != null && rawBody.trim().startsWith("{")) {
                // Unwrap the JSON if Feign added diagnostic brackets
                int lastOpen = rawBody.lastIndexOf("[");
                int lastClose = rawBody.lastIndexOf("]");
                String jsonPart = (lastOpen != -1 && lastClose > lastOpen)
                        ? rawBody.substring(lastOpen + 1, lastClose)
                        : rawBody;

                // Use readValue to avoid the metadata "boolean flags" issue
                Map<String, Object> downstreamError = new ObjectMapper().readValue(jsonPart, Map.class);
                return ResponseEntity.status(status).body(downstreamError);
            }
        } catch (Exception ex) {
            // Fallback to your existing logic if parsing fails
        }

        return ResponseEntity.status(status).body(buildBody(HttpStatus.valueOf(status), "Downstream Error", rawBody));
    }
    private Map<String, Object> buildBody(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message != null ? message : "Unexpected error");
        return body;
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("Bad Request / Data Not Found: {}", ex.getMessage());
        return buildErrorResponse(ex.getMessage(), request.getDescription(false), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            fieldErrors.put(fieldName, violation.getMessage());
        });

        log.error("Parameter Validation failed: {}", fieldErrors);
        return buildErrorResponse("Invalid request parameters.", request.getDescription(false), HttpStatus.BAD_REQUEST, fieldErrors);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        log.error("DTO Validation failed: {}", fieldErrors);
        return buildErrorResponse("Please check the input fields and try again.", request.getDescription(false), HttpStatus.BAD_REQUEST, fieldErrors);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(String message, String details, HttpStatus status, Map<String, String> fieldErrors) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", message);
        errorResponse.put("details", details);

        if (fieldErrors != null && !fieldErrors.isEmpty()) {
            errorResponse.put("fieldErrors", fieldErrors);
        }

        return new ResponseEntity<>(errorResponse, status);
    }
}