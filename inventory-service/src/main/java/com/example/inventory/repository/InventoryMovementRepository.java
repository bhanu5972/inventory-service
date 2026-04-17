package com.example.inventory.repository;

import com.example.inventory.entity.InventoryMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, UUID> {
    List<InventoryMovement> findByOrderId(UUID orderId);
    List<InventoryMovement> findByProductId(UUID productId);
}