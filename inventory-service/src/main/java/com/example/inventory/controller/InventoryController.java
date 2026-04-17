package com.example.inventory.controller;

import com.example.inventory.dto.ReserveRequest;
import com.example.inventory.dto.ReserveResponse;
import com.example.inventory.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/inventory")
@Tag(name = "Inventory Management", description = "Endpoints for managing inventory reservations")
public class InventoryController {
    
    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);
    
    private final InventoryService inventoryService;
    
    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    
    @PostMapping("/reserve")
    @Operation(summary = "Reserve stock", description = "Reserve stock for an order with TTL")
    public ResponseEntity<ReserveResponse> reserveStock(@Valid @RequestBody ReserveRequest request) {
        log.info("POST /v1/inventory/reserve - orderId: {}", request.getOrderId());
        ReserveResponse response = inventoryService.reserveStock(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @PostMapping("/release/{reservationId}")
    @Operation(summary = "Release reservation", description = "Release a previously reserved stock")
    public ResponseEntity<Void> releaseReservation(@PathVariable String reservationId) {
        log.info("POST /v1/inventory/release/{}", reservationId);
        inventoryService.releaseReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/ship")
    @Operation(summary = "Ship stock", description = "Ship stock after packing")
    public ResponseEntity<Void> shipStock(@RequestParam UUID orderId,
                                           @RequestParam UUID productId,
                                           @RequestParam String warehouse,
                                           @RequestParam Integer quantity) {
        log.info("POST /v1/inventory/ship - orderId: {}, productId: {}", orderId, productId);
        inventoryService.shipStock(orderId, productId, warehouse, quantity);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{productId}/{warehouse}")
    @Operation(summary = "Get inventory", description = "Get inventory for a product in a warehouse")
    public ResponseEntity<?> getInventory(@PathVariable UUID productId, 
                                           @PathVariable String warehouse) {
        var inventory = inventoryService.getInventory(productId, warehouse);
        if (inventory == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(inventory);
    }
}