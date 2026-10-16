
package com.ecommerce.inventory.repository;

import com.ecommerce.inventory.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    boolean existsByProductId(Long productId);

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.availableQuantity = i.availableQuantity - :quantity,
            i.reservedQuantity = i.reservedQuantity + :quantity
        WHERE i.productId = :productId
          AND i.availableQuantity >= :quantity
    """)
    int reserveStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );

    @Modifying
    @Query("""
        UPDATE Inventory i
        SET i.availableQuantity = i.availableQuantity + :quantity,
            i.reservedQuantity = i.reservedQuantity - :quantity
        WHERE i.productId = :productId
          AND i.reservedQuantity >= :quantity
    """)
    int releaseStock(
            @Param("productId") Long productId,
            @Param("quantity") Integer quantity
    );
}
