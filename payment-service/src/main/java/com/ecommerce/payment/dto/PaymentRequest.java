package com.ecommerce.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotNull @Positive Long orderId,
        @NotNull @Positive Long userId,
        @NotNull @Positive BigDecimal amount,
        @NotBlank String paymentMethod,
        boolean simulateFailure
) {
}