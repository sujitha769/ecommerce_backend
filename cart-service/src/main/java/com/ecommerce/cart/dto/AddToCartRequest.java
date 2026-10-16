package com.ecommerce.cart.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddToCartRequest(
        @NotNull @Positive Long productId,
        @NotNull @Positive Integer quantity
) {
}