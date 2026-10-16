package com.ecommerce.order.entity;

public enum OrderStatus {
    PLACED,
    PENDING_PAYMENT,
    CONFIRMED,
    AWAITING_PAYMENT_ON_DELIVERY,
    CANCELLED
}