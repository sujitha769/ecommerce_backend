
package com.ecommerce.product.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Map;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(
            RestClient.Builder builder,
            @Value("${inventory.service.url:http://localhost:8803}")
            String inventoryServiceUrl) {

        this.restClient = builder
                .baseUrl(inventoryServiceUrl)
                .build();
    }

    public void createInventory(Long productId, Integer initialQuantity) {
        System.out.println(
                "Creating inventory: productId=" + productId
                        + ", initialQuantity=" + initialQuantity
        );

        try {
            restClient.post()
                    .uri("/api/inventory/{productId}", productId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("initialQuantity", initialQuantity))
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException exception) {
            System.err.println(
                    "Inventory HTTP status: " + exception.getStatusCode()
            );
            System.err.println(
                    "Inventory response body: "
                            + exception.getResponseBodyAsString()
            );
            throw exception;
        }
    }

    /**
     * Creates inventory only if no record exists.
     *
     * @return true if created; false if inventory already exists
     */
    public boolean ensureInventoryExists(
            Long productId,
            Integer initialQuantity) {

        HttpStatusCode status = restClient.get()
                .uri("/api/inventory/{productId}", productId)
                .exchange((request, response) -> response.getStatusCode());

        if (status.is2xxSuccessful()) {
            return false;
        }

        if (status.value() == 404) {
            createInventory(productId, initialQuantity);
            return true;
        }

        throw new IllegalStateException(
                "Could not check inventory for product ID: " + productId
                        + ". Inventory Service returned: " + status.value()
        );
    }
}
