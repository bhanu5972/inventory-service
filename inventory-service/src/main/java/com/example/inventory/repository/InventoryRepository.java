package com.example.inventory.repository;

import com.example.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {
    
    Optional<Inventory> findByProductIdAndWarehouse(UUID productId, String warehouse);
    
    List<Inventory> findByProductId(UUID productId);
    
    List<Inventory> findByWarehouse(String warehouse);
    
    @Modifying
    @Query(value = """
        UPDATE inventory 
        SET reserved = reserved + :quantity,
            updated_at = NOW()
        WHERE product_id = :productId 
            AND warehouse = :warehouse 
            AND (on_hand - reserved) >= :quantity
        """, nativeQuery = true)
    int reserveStock(@Param("productId") UUID productId, 
                     @Param("warehouse") String warehouse, 
                     @Param("quantity") Integer quantity);
    
    @Modifying
    @Query(value = """
        UPDATE inventory 
        SET reserved = reserved - :quantity,
            updated_at = NOW()
        WHERE product_id = :productId 
            AND warehouse = :warehouse 
            AND reserved >= :quantity
        """, nativeQuery = true)
    int releaseStock(@Param("productId") UUID productId, 
                     @Param("warehouse") String warehouse, 
                     @Param("quantity") Integer quantity);
    
    @Modifying
    @Query(value = """
        UPDATE inventory 
        SET on_hand = on_hand - :quantity,
            reserved = reserved - :quantity,
            updated_at = NOW()
        WHERE product_id = :productId 
            AND warehouse = :warehouse 
            AND on_hand >= :quantity 
            AND reserved >= :quantity
        """, nativeQuery = true)
    int shipStock(@Param("productId") UUID productId, 
                  @Param("warehouse") String warehouse, 
                  @Param("quantity") Integer quantity);
}