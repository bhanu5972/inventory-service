package com.example.inventory.dto;

import java.time.Instant;

public class ErrorResponse {
    private String code;
    private String message;
    private String correlationId;
    private Instant timestamp;
    private String path;
    private Integer status;
    
    public ErrorResponse() {}
    
    // Getters and Setters
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getCorrelationId() { return correlationId; }
    public void setCorrelationId(String correlationId) { this.correlationId = correlationId; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}