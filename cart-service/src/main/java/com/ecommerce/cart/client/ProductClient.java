package com.ecommerce.cart.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProductClient {

    private final RestClient restClient;

    public ProductClient(
            RestClient.Builder builder,
            @Value("${product.service.url}") String serviceUrl) {

        this.restClient = builder.baseUrl(serviceUrl).build();
    }

    public void verifyProductExists(Long productId) {
        try {
            restClient.get()
                    .uri("/api/products/{id}", productId)
                    .retrieve()
                    .toBodilessEntity();

        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product not found: " + productId
                );
            }

            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Product Service returned an error"
            );

        } catch (Exception ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "Product Service is unavailable"
            );
        }
    }
}