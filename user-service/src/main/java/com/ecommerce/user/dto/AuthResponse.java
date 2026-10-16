package com.ecommerce.user.dto;

public record AuthResponse(
        String message,
        String tokenType,
        String accessToken,
        Long userId,
        String name,
        String email
) {}