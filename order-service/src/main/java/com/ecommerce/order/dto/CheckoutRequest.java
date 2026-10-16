package com.ecommerce.order.dto;

import jakarta.validation.constraints.NotBlank;

public record CheckoutRequest(
        @NotBlank String paymentMethod,
        boolean simulateFailure
) {
}