package com.cognizant.taxpayerService.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the Taxpayer Service.
 * This class also defines all service-specific exception types.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TaxpayerNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleTaxpayerNotFound(TaxpayerNotFoundException ex) {
        Map<String, Object> body = buildBody(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentNotFound(DocumentNotFoundException ex) {
        Map<String, Object> body = buildBody(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(DocumentOwnershipException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentOwnership(DocumentOwnershipException ex) {
        Map<String, Object> body = buildBody(HttpStatus.FORBIDDEN, "Forbidden", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(DocumentDeletionException.class)
    public ResponseEntity<Map<String, Object>> handleDocumentDeletion(DocumentDeletionException ex) {
        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(InvalidDocumentStatusException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidDocumentStatus(InvalidDocumentStatusException ex) {
        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(UserServiceException.class)
    public ResponseEntity<Map<String, Object>> handleUserService(UserServiceException ex) {
        Map<String, Object> body = buildBody(HttpStatus.SERVICE_UNAVAILABLE, "Service Unavailable", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ValidationException ex) {
        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<Map<String, Object>> handleDatabase(DatabaseException ex) {
        Map<String, Object> body = buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Map<String, Object>> handleFileUpload(FileUploadException ex) {
        Map<String, Object> body = buildBody(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Error");

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });

        body.put("message", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(RuntimeException ex) {
        String message = ex.getMessage();
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String error = "Internal Server Error";

        if (message != null) {
            if (message.contains("already exists") || message.contains("Account already exists")) {
                status = HttpStatus.CONFLICT;
                error = "Conflict";
            } else if (message.contains("does not belong") || message.contains("Document does not exist") || message.contains("Taxpayer not found")) {
                status = HttpStatus.NOT_FOUND;
                error = "Not Found";
            } else if (message.contains("can only be deleted if") || message.contains("Cannot update")) {
                status = HttpStatus.FORBIDDEN;
                error = "Forbidden";
            } else if (message.contains("File URI is required") || message.contains("Unable to generate")) {
                status = HttpStatus.INTERNAL_SERVER_ERROR;
                error = "Internal Server Error";
            }
        }

        Map<String, Object> body = buildBody(status, error, message);
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneral(Exception ex) {
        Map<String, Object> body = buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private Map<String, Object> buildBody(HttpStatus status, String error, String message) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", error);
        body.put("message", message != null ? message : "Unexpected error");
        return body;
    }

    public static class GlobalServiceException extends RuntimeException {
        public GlobalServiceException(String message) {
            super(message);
        }

        public GlobalServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class TaxpayerNotFoundException extends GlobalServiceException {
        public TaxpayerNotFoundException(String message) {
            super(message);
        }

        public TaxpayerNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class DocumentNotFoundException extends GlobalServiceException {
        public DocumentNotFoundException(String message) {
            super(message);
        }

        public DocumentNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class DocumentOwnershipException extends GlobalServiceException {
        public DocumentOwnershipException(String message) {
            super(message);
        }

        public DocumentOwnershipException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class DocumentDeletionException extends GlobalServiceException {
        public DocumentDeletionException(String message) {
            super(message);
        }

        public DocumentDeletionException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class InvalidDocumentStatusException extends GlobalServiceException {
        public InvalidDocumentStatusException(String message) {
            super(message);
        }

        public InvalidDocumentStatusException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class UserServiceException extends GlobalServiceException {
        public UserServiceException(String message) {
            super(message);
        }

        public UserServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ValidationException extends GlobalServiceException {
        public ValidationException(String message) {
            super(message);
        }

        public ValidationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class DatabaseException extends GlobalServiceException {
        public DatabaseException(String message) {
            super(message);
        }

        public DatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class FileUploadException extends GlobalServiceException {
        public FileUploadException(String message) {
            super(message);
        }

        public FileUploadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}