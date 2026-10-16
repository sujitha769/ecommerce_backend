
package com.ecommerce.cart.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class InventoryClient {

    private final RestClient restClient;

    public InventoryClient(
            RestClient.Builder builder,
            @Value("${inventory.service.url:http://localhost:8803}")
            String serviceUrl) {

        this.restClient = builder.baseUrl(serviceUrl).build();
    }

    public Integer getAvailableQuantity(Long productId) {
        try {
            InventoryResponse response = restClient.get()
                    .uri("/api/inventory/{productId}", productId)
                    .retrieve()
                    .body(InventoryResponse.class);

            if (response == null || response.availableQuantity() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_GATEWAY,
                        "Inventory Service returned an invalid response"
                );
            }

            return response.availableQuantity();

        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Inventory record not found for product "
                                + productId
                                + ". Initialize its inventory first."
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Inventory Service returned HTTP "
                            + ex.getStatusCode().value()
            );

        } catch (ResponseStatusException ex) {
            throw ex;

        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Unable to connect to Inventory Service"
            );
        }
    }

    private record InventoryResponse(
            Long id,
            Long productId,
            Integer availableQuantity,
            Integer reservedQuantity
    ) {
    }
}
