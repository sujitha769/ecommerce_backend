
package com.ecommerce.order.client;

import com.ecommerce.order.dto.CartItemResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class CartClient {

    private final RestClient client;

    public CartClient(
            @Qualifier("cartRestClient") RestClient client
    ) {
        this.client = client;
    }

    public List<CartItemResponse> getCart(Long userId) {
        CartItemResponse[] response = client.get()
                .uri("/api/cart/{userId}", userId)
                .retrieve()
                .body(CartItemResponse[].class);

        return response == null ? List.of() : List.of(response);
    }

    public void clearCart(Long userId) {
        client.delete()
                .uri("/api/cart/{userId}", userId)
                .retrieve()
                .toBodilessEntity();
    }
}
