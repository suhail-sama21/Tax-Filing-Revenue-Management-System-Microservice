package com.cognizant.authenticationService.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final ObjectMapper objectMapper;

    @ExceptionHandler(CustomDownStreamException.class)
    public ResponseEntity<Object> handleCustomDownstream(CustomDownStreamException e) {
        try {
            String rawJson = e.getCleanJson();
            log.info(rawJson);
            if (rawJson == null || rawJson.isBlank()) {
                return ResponseEntity.status(e.getStatus()).body(Map.of("message", "Downstream error occurred"));
            }

            // FIX: Read the JSON into a standard Java Object/Map instead of a JsonNode
            Object body = objectMapper.readValue(rawJson, Object.class);

            return ResponseEntity.status(e.getStatus()).body(body);
        } catch (Exception ex) {
            log.error("Failed to parse JSON: {}", ex.getMessage());
            return ResponseEntity.status(e.getStatus()).body(e.getCleanJson());
        }
    }

    @ExceptionHandler(org.springframework.security.authentication.InternalAuthenticationServiceException.class)
    public ResponseEntity<Object> handleInternalAuthServiceException(InternalAuthenticationServiceException ex) {
        log.error("Internal Auth Service Exception caught: {}", ex.getMessage());

        // 1. Check if the "cause" of this error is our CustomDownStreamException
        if (ex.getCause() instanceof CustomDownStreamException downstreamEx) {
            log.info("Found CustomDownStreamException inside wrapper, delegating...");
            return handleCustomDownstream(downstreamEx);
        }

        // 2. Otherwise, treat it as a Bad Credentials error
        return handleBadCredentials(new BadCredentialsException("Invalid email or password"));
    }

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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Object> handleRuntime(RuntimeException ex) {
        log.info("INTERCEPTED: RuntimeException");

        String message = ex.getMessage();

        // 3. Normal business logic for local exceptions
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now());
        int status = 500;
        String error = "Internal Server Error";

        if (message != null) {
            if (message.contains("already exists")) {
                status = 409;
                error = "Conflict";
            } else if (message.contains("does not belong")) {
                status = 404;
                error = "Not Found";
            } else if (message.contains("Unauthorized") || message.contains("401")) {
                status = 401;
                error = "Unauthorized";
            }
        }

        body.put("status", status);
        body.put("error", error);
        body.put("message", message);

        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", 400);
        body.put("error", "Validation Error");

        // Extract the specific field errors
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        body.put("message", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
