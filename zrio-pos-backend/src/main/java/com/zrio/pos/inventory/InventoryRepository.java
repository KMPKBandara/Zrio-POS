package com.zrio.pos.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository
        extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(
            Long productId
    );

    List<Inventory>
    findByProductActiveTrueOrderByProductNameAsc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i
        FROM Inventory i
        WHERE i.productId = :productId
        """)
    Optional<Inventory> findByProductIdForUpdate(
            @Param("productId") Long productId
    );

    @Query("""
        SELECT i
        FROM Inventory i
        WHERE i.product.active = true
          AND i.quantityOnHand > 0
          AND i.quantityOnHand
                <= i.product.lowStockThreshold
        ORDER BY i.product.name ASC
        """)
    List<Inventory> findLowStockActiveInventory();

    List<Inventory>
    findByProductActiveTrueAndQuantityOnHandOrderByProductNameAsc(
            int quantityOnHand
    );

    @Query("""
        SELECT i
        FROM Inventory i
        WHERE i.product.active = true
          AND (
              LOWER(i.product.name)
                  LIKE LOWER(CONCAT('%', :query, '%'))
              OR
              LOWER(i.product.itemCode)
                  LIKE LOWER(CONCAT('%', :query, '%'))
          )
        ORDER BY i.product.name ASC
        """)
    List<Inventory> searchActiveInventory(
            @Param("query") String query
    );
}