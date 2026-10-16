package com.ecommerce.order.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class PaymentClient {

    private final RestClient client;

    public PaymentClient(
            RestClient.Builder builder,
            @Value("${services.payment.url:http://localhost:8807}")
            String paymentUrl
    ) {
        this.client = builder.baseUrl(paymentUrl).build();
    }

    public PaymentResponse processPayment(
            Long orderId,
            Long userId,
            BigDecimal amount,
            String paymentMethod,
            boolean simulateFailure
    ) {
        return client.post()
                .uri("/api/payments")
                .body(new PaymentRequest(
                        orderId,
                        userId,
                        amount,
                        paymentMethod,
                        simulateFailure
                ))
                .retrieve()
                .body(PaymentResponse.class);
    }

    public record PaymentRequest(
            Long orderId,
            Long userId,
            BigDecimal amount,
            String paymentMethod,
            boolean simulateFailure
    ) {
    }

    public record PaymentResponse(
            Long id,
            Long orderId,
            Long userId,
            BigDecimal amount,
            String paymentMethod,
            String status,
            String transactionId,
            java.time.LocalDateTime createdAt
    ) {
    }
}