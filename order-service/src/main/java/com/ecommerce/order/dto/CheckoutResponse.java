package com.ecommerce.order.dto;

public record CheckoutResponse(
        String message,
        OrderResponse order
) {}