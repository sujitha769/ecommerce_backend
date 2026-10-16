package com.ecommerce.order.controller;

import com.ecommerce.order.dto.CheckoutRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.service.OrderService;
import com.ecommerce.order.dto.CheckoutRequest;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout/{userId}")
    public ResponseEntity<OrderResponse> checkout(
            @PathVariable Long userId,
            @Valid @RequestBody CheckoutRequest request
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.checkout(userId, request));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrders(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                orderService.getOrdersByUser(userId)
        );
    }

    @GetMapping("/{orderId}/user/{userId}")
    public ResponseEntity<OrderResponse> getOrder(
            @PathVariable Long orderId,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                orderService.getOrderById(orderId, userId)
        );
    }
}