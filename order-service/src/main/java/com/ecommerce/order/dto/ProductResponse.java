package com.ecommerce.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        String category,
        BigDecimal price,
        BigDecimal mrp,
        Double rating,
        Integer reviews,
        String emoji,
        List<String> bg,
        String description,
        List<String> sizes,
        String imageUrl
) {}