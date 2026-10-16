package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductClient {

    private final RestClient client;

    public ProductClient(
            @Qualifier("productRestClient") RestClient client) {
        this.client = client;
    }

    public ProductResponse getProduct(Long productId) {
        return client.get()
                .uri("/api/products/{id}", productId)
                .retrieve()
                .body(ProductResponse.class);
    }
}