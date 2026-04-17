package com.example.inventory.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_movements")
public class InventoryMovement {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "movement_id")
    private UUID movementId;
    
    @Column(name = "product_id", nullable = false)
    private UUID productId;
    
    @Column(nullable = false, length = 50)
    private String warehouse;
    
    @Column(name = "order_id")
    private UUID orderId;
    
    @Column(nullable = false, length = 20)
    private String type;  // RESERVE, RELEASE, SHIP
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "reservation_id")
    private String reservationId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    // Default constructor
    public InventoryMovement() {}
    
    // Constructor with fields
    public InventoryMovement(UUID productId, String warehouse, UUID orderId, 
                             String type, Integer quantity, String reservationId) {
        this.productId = productId;
        this.warehouse = warehouse;
        this.orderId = orderId;
        this.type = type;
        this.quantity = quantity;
        this.reservationId = reservationId;
    }
    
    // Getters and Setters
    public UUID getMovementId() { return movementId; }
    public void setMovementId(UUID movementId) { this.movementId = movementId; }
    
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }
    
    public String getWarehouse() { return warehouse; }
    public void setWarehouse(String warehouse) { this.warehouse = warehouse; }
    
    public UUID getOrderId() { return orderId; }
    public void setOrderId(UUID orderId) { this.orderId = orderId; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    
    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}