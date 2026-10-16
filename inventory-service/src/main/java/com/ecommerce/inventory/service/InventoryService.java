
package com.ecommerce.inventory.service;

import com.ecommerce.inventory.dto.InventoryCreateRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.dto.StockRequest;
import com.ecommerce.inventory.entity.Inventory;
import com.ecommerce.inventory.repository.InventoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }



    @Transactional
    public InventoryResponse createInventory(
            Long productId,
            InventoryCreateRequest request
    ) {
        long startTime = System.nanoTime();

        if (productId == null || productId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Product ID must be positive"
            );
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setAvailableQuantity(request.getInitialQuantity());
        inventory.setReservedQuantity(0);

        try {
            Inventory savedInventory =
                    inventoryRepository.saveAndFlush(inventory);

            long elapsedMs =
                    (System.nanoTime() - startTime) / 1_000_000;

            System.out.println(
                    "[INVENTORY TIMING] productId=" + productId
                            + " | createAndFlush=" + elapsedMs + " ms"
            );

            return InventoryResponse.from(savedInventory);

        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Inventory already exists for product " + productId,
                    ex
            );
        }
    }



    @Transactional(readOnly = true)
    public InventoryResponse getInventory(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory not found for product " + productId
                ));

        return InventoryResponse.from(inventory);
    }

    @Transactional
    public InventoryResponse reserveStock(
            Long productId,
            StockRequest request
    ) {
        int updated = inventoryRepository.reserveStock(
                productId, request.quantity()
        );

        if (updated == 0) {
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Inventory not found for product " + productId
                    ));

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insufficient available stock. Available: "
                            + inventory.getAvailableQuantity()
            );
        }

        return getInventory(productId);
    }

    @Transactional
    public InventoryResponse releaseStock(
            Long productId,
            StockRequest request
    ) {
        int updated = inventoryRepository.releaseStock(
                productId, request.quantity()
        );

        if (updated == 0) {
            Inventory inventory = inventoryRepository.findByProductId(productId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Inventory not found for product " + productId
                    ));

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot release this quantity. Reserved stock: "
                            + inventory.getReservedQuantity()
            );
        }

        return getInventory(productId);
    }
}
