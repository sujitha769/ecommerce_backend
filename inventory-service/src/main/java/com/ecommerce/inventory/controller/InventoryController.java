
package com.ecommerce.inventory.controller;

import com.ecommerce.inventory.dto.InventoryCreateRequest;
import com.ecommerce.inventory.dto.InventoryResponse;
import com.ecommerce.inventory.dto.StockRequest;
import com.ecommerce.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<InventoryResponse> createInventory(
            @PathVariable Long productId,
            @Valid @RequestBody InventoryCreateRequest request
    ) {
        InventoryResponse response =
                inventoryService.createInventory(productId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getInventory(
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(inventoryService.getInventory(productId));
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<InventoryResponse> reserveStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockRequest request
    ) {
        return ResponseEntity.ok(
                inventoryService.reserveStock(productId, request)
        );
    }

    @PostMapping("/{productId}/release")
    public ResponseEntity<InventoryResponse> releaseStock(
            @PathVariable Long productId,
            @Valid @RequestBody StockRequest request
    ) {
        return ResponseEntity.ok(
                inventoryService.releaseStock(productId, request)
        );
    }
}
