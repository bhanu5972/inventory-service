package com.example.inventory.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class ReserveResponse {
    private String reservationId;
    private Boolean success;
    private Instant expiresAt;
    private List<ReservedItem> reservedItems;
    private String message;
    
    // Default constructor
    public ReserveResponse() {}
    
    // Getters and Setters
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    
    public Boolean getSuccess() { return success; }
    public void setSuccess(Boolean success) { this.success = success; }
    
    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }
    
    public List<ReservedItem> getReservedItems() { return reservedItems; }
    public void setReservedItems(List<ReservedItem> reservedItems) { this.reservedItems = reservedItems; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    // Inner class
    public static class ReservedItem {
        private UUID productId;
        private String warehouse;
        private Integer quantity;
        
        public ReservedItem() {}
        
        public ReservedItem(UUID productId, String warehouse, Integer quantity) {
            this.productId = productId;
            this.warehouse = warehouse;
            this.quantity = quantity;
        }
        
        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }
        
        public String getWarehouse() { return warehouse; }
        public void setWarehouse(String warehouse) { this.warehouse = warehouse; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
    }
}