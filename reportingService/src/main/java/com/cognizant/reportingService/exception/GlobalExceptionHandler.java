package com.cognizant.reportingService.exception;

import feign.FeignException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, Object>> handleFeignException(FeignException ex, WebRequest request) {
        log.error("Feign Client Exception: {}", ex.getMessage());

        String message = "Error communicating with downstream microservice: " + ex.getMessage();
        int status = ex.status() >= 400 ? ex.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

        return buildErrorResponse(message, request.getDescription(false), HttpStatus.valueOf(status), null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex, WebRequest request) {
        log.error("Unexpected Error: ", ex);
        return buildErrorResponse("An unexpected error occurred: " + ex.getMessage(), request.getDescription(false), HttpStatus.INTERNAL_SERVER_ERROR, null);
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