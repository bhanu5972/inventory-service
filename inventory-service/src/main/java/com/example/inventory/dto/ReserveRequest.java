package com.example.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public class ReserveRequest {
    
    @NotNull(message = "Order ID is required")
    private UUID orderId;
    
    @NotNull(message = "Items are required")
    private List<ItemReservation> items;
    
    private String preferredWarehouse;
    
    // Default constructor
    public ReserveRequest() {}
    
    // Constructor with fields
    public ReserveRequest(UUID orderId, List<ItemReservation> items, String preferredWarehouse) {
        this.orderId = orderId;
        this.items = items;
        this.preferredWarehouse = preferredWarehouse;
    }
    
    // Getters and Setters
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    
    public List<ItemReservation> getItems() { return items; }
    public void setItems(List<ItemReservation> items) { this.items = items; }
    
    public String getPreferredWarehouse() { return preferredWarehouse; }
    public void setPreferredWarehouse(String preferredWarehouse) { this.preferredWarehouse = preferredWarehouse; }
    
    // Inner class
    public static class ItemReservation {
        private UUID productId;
        private Integer quantity;
        private String warehouse;
        
        public ItemReservation() {}
        
        public ItemReservation(UUID productId, Integer quantity, String warehouse) {
            this.productId = productId;
            this.quantity = quantity;
            this.warehouse = warehouse;
        }
        
        public UUID getProductId() { return productId; }
        public void setProductId(UUID productId) { this.productId = productId; }
        
        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }
        
        public String getWarehouse() { return warehouse; }
        public void setWarehouse(String warehouse) { this.warehouse = warehouse; }
    }
}