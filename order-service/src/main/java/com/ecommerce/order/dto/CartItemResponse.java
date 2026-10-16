package com.ecommerce.order.dto;

public record CartItemResponse(
        Long id,
        Long userId,
        Long productId,
        Integer quantity
) {}