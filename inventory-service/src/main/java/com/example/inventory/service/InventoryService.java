package com.example.inventory.service;

import com.example.inventory.dto.ReserveRequest;
import com.example.inventory.dto.ReserveResponse;
import com.example.inventory.entity.Inventory;
import com.example.inventory.entity.InventoryMovement;
import com.example.inventory.entity.Reservation;
import com.example.inventory.repository.InventoryMovementRepository;
import com.example.inventory.repository.InventoryRepository;
import com.example.inventory.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class InventoryService {
    
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);
    
    private final InventoryRepository inventoryRepository;
    private final InventoryMovementRepository movementRepository;
    private final ReservationRepository reservationRepository;
    
    @Value("${inventory.reservation.ttl-ms:900000}")
    private long reservationTtlMs;
    
    public InventoryService(InventoryRepository inventoryRepository,
                            InventoryMovementRepository movementRepository,
                            ReservationRepository reservationRepository) {
        this.inventoryRepository = inventoryRepository;
        this.movementRepository = movementRepository;
        this.reservationRepository = reservationRepository;
    }
    
    @Transactional
    public ReserveResponse reserveStock(ReserveRequest request) {
        String reservationId = UUID.randomUUID().toString();
        log.info("Reserving stock for order: {}, reservationId: {}", request.getOrderId(), reservationId);
        
        List<ReserveResponse.ReservedItem> reservedItems = new ArrayList<>();
        
        for (ReserveRequest.ItemReservation item : request.getItems()) {
            String warehouse = determineWarehouse(item, request.getPreferredWarehouse());
            
            // Atomic reservation update
            int updated = inventoryRepository.reserveStock(
                item.getProductId(), warehouse, item.getQuantity());
            
            if (updated == 0) {
                throw new RuntimeException("Insufficient stock for product: " + item.getProductId());
            }
            
            // Record movement
            InventoryMovement movement = new InventoryMovement(
                item.getProductId(), warehouse, request.getOrderId(),
                "RESERVE", item.getQuantity(), reservationId);
            movementRepository.save(movement);
            
            reservedItems.add(new ReserveResponse.ReservedItem(
                item.getProductId(), warehouse, item.getQuantity()));
        }
        
        // Save reservation with TTL
        Instant expiresAt = Instant.now().plusMillis(reservationTtlMs);
        Reservation reservation = new Reservation(reservationId, request.getOrderId(), expiresAt);
        reservationRepository.save(reservation);
        
        log.info("Stock reserved successfully for order: {}, expires at: {}", request.getOrderId(), expiresAt);
        
        ReserveResponse response = new ReserveResponse();
        response.setReservationId(reservationId);
        response.setSuccess(true);
        response.setExpiresAt(expiresAt);
        response.setReservedItems(reservedItems);
        return response;
    }
    
    @Transactional
    public void releaseReservation(String reservationId) {
        log.info("Releasing reservation: {}", reservationId);
        
        Reservation reservation = reservationRepository.findById(reservationId).orElse(null);
        if (reservation == null) {
            log.warn("Reservation not found: {}", reservationId);
            return;
        }
        
        // Get all movements for this reservation
        List<InventoryMovement> movements = movementRepository.findByOrderId(reservation.getOrderId());
        
        for (InventoryMovement movement : movements) {
            if ("RESERVE".equals(movement.getType()) && reservationId.equals(movement.getReservationId())) {
                int updated = inventoryRepository.releaseStock(
                    movement.getProductId(), movement.getWarehouse(), movement.getQuantity());
                
                if (updated > 0) {
                    InventoryMovement releaseMovement = new InventoryMovement(
                        movement.getProductId(), movement.getWarehouse(), reservation.getOrderId(),
                        "RELEASE", movement.getQuantity(), reservationId);
                    movementRepository.save(releaseMovement);
                }
            }
        }
        
        reservationRepository.deleteByReservationId(reservationId);
        log.info("Reservation released: {}", reservationId);
    }
    
    @Transactional
    public void shipStock(UUID orderId, UUID productId, String warehouse, Integer quantity) {
        log.info("Shipping stock for order: {}, product: {}, warehouse: {}, quantity: {}", 
                 orderId, productId, warehouse, quantity);
        
        int updated = inventoryRepository.shipStock(productId, warehouse, quantity);
        
        if (updated > 0) {
            InventoryMovement shipmentMovement = new InventoryMovement(
                productId, warehouse, orderId, "SHIP", quantity, null);
            movementRepository.save(shipmentMovement);
            log.info("Stock shipped successfully");
        }
    }
    
    @Scheduled(fixedDelayString = "${inventory.cleanup.interval-ms:60000}")
    @Transactional
    public void releaseExpiredReservations() {
        log.info("Running expired reservations cleanup");
        
        List<Reservation> expiredReservations = reservationRepository.findExpiredReservations(Instant.now());
        
        for (Reservation reservation : expiredReservations) {
            log.info("Releasing expired reservation: {}", reservation.getReservationId());
            releaseReservation(reservation.getReservationId());
        }
        
        if (!expiredReservations.isEmpty()) {
            log.info("Released {} expired reservations", expiredReservations.size());
        }
    }
    
    private String determineWarehouse(ReserveRequest.ItemReservation item, String preferredWarehouse) {
        if (preferredWarehouse != null && isStockAvailable(item.getProductId(), preferredWarehouse, item.getQuantity())) {
            return preferredWarehouse;
        }
        
        // Find any warehouse with sufficient stock
        List<Inventory> inventories = inventoryRepository.findByProductId(item.getProductId());
        for (Inventory inv : inventories) {
            if (inv.getAvailableQuantity() >= item.getQuantity()) {
                return inv.getWarehouse();
            }
        }
        
        // If no warehouse found, throw exception
        throw new RuntimeException("No warehouse with sufficient stock for product: " + item.getProductId());
    }
    
    private boolean isStockAvailable(UUID productId, String warehouse, Integer quantity) {
        return inventoryRepository.findByProductIdAndWarehouse(productId, warehouse)
            .map(inv -> inv.getAvailableQuantity() >= quantity)
            .orElse(false);
    }
    
    @Transactional(readOnly = true)
    public Inventory getInventory(UUID productId, String warehouse) {
        return inventoryRepository.findByProductIdAndWarehouse(productId, warehouse)
            .orElse(null);
    }
}