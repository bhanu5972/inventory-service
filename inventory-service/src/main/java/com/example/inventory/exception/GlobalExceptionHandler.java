package com.example.inventory.exception;

import com.example.inventory.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.Instant;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private String getCorrelationId(WebRequest request) {
        String correlationId = request.getHeader("X-Correlation-Id");
        return correlationId != null ? correlationId : UUID.randomUUID().toString();
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("Runtime exception: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse();
        error.setCode("INSUFFICIENT_STOCK");
        error.setMessage(ex.getMessage());
        error.setCorrelationId(getCorrelationId(request));
        error.setTimestamp(Instant.now());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(HttpStatus.CONFLICT.value());
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error", ex);
        
        ErrorResponse error = new ErrorResponse();
        error.setCode("INTERNAL_SERVER_ERROR");
        error.setMessage("An unexpected error occurred");
        error.setCorrelationId(getCorrelationId(request));
        error.setTimestamp(Instant.now());
        error.setPath(request.getDescription(false).replace("uri=", ""));
        error.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}