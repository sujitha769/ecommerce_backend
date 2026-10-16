
package com.ecommerce.order.client;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryClient {

    private final RestClient client;

    public InventoryClient(
            @Qualifier("inventoryRestClient") RestClient client
    ) {
        this.client = client;
    }

    public InventoryResponse getInventory(Long productId) {
        return client.get()
                .uri("/api/inventory/{productId}", productId)
                .retrieve()
                .body(InventoryResponse.class);
    }

    public Integer getAvailableQuantity(Long productId) {
        InventoryResponse response = getInventory(productId);

        if (response == null) {
            throw new IllegalStateException(
                    "Inventory response is empty for product " + productId
            );
        }

        return response.availableQuantity();
    }

    public void reserveStock(Long productId, Integer quantity) {
        client.post()
                .uri("/api/inventory/{productId}/reserve", productId)
                .body(new StockRequest(quantity))
                .retrieve()
                .toBodilessEntity();
    }

    public void releaseStock(Long productId, Integer quantity) {
        client.post()
                .uri("/api/inventory/{productId}/release", productId)
                .body(new StockRequest(quantity))
                .retrieve()
                .toBodilessEntity();
    }

    public record StockRequest(Integer quantity) {
    }

    public record InventoryResponse(
            Long id,
            Long productId,
            Integer availableQuantity,
            Integer reservedQuantity
    ) {
    }
}
