package com.example.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"product_id", "warehouse"})
})
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "inventory_id")
    private UUID inventoryId;
    
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    
    @Column(nullable = false, length = 50)
    private String warehouse;
    
    @Column(name = "on_hand", nullable = false)
    private Integer onHand = 0;
    
    @Column(nullable = false)
    private Integer reserved = 0;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PreUpdate
    @PrePersist
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Default constructor
    public Inventory() {}
    
    // Constructor with fields
    public Inventory(UUID productId, String warehouse, Integer onHand, Integer reserved) {
        this.productId = productId;
        this.warehouse = warehouse;
        this.onHand = onHand;
        this.reserved = reserved;
    }
    
    // Getters and Setters
    public UUID getInventoryId() { return inventoryId; }
    public void setInventoryId(UUID inventoryId) { this.inventoryId = inventoryId; }
    
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    
    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }
    
    public Integer getOnHand() { return onHand; }
    public void setOnHand(Integer onHand) { this.onHand = onHand; }
    
    public Integer getReserved() { return reserved; }
    public void setReserved(Integer reserved) { this.reserved = reserved; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Business method
    public Integer getAvailableQuantity() {
        return onHand - reserved;
    }
}