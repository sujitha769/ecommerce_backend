package com.ecommerce.cart.dto;

import com.ecommerce.cart.entity.CartItem;
import java.time.LocalDateTime;

public record CartItemResponse(
        Long id,
        Long userId,
        Long productId,
        Integer quantity,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static CartItemResponse from(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getUserId(),
                item.getProductId(),
                item.getQuantity(),
                item.getCreatedAt(),
                item.getUpdatedAt()
        );
    }
}