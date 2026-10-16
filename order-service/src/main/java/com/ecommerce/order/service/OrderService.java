package com.ecommerce.order.service;

import com.ecommerce.order.client.CartClient;
import com.ecommerce.order.client.InventoryClient;
import com.ecommerce.order.client.PaymentClient;
import com.ecommerce.order.client.ProductClient;
import com.ecommerce.order.dto.CartItemResponse;
import com.ecommerce.order.dto.CheckoutRequest;
import com.ecommerce.order.dto.OrderResponse;
import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartClient cartClient;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;

    public OrderService(
            OrderRepository orderRepository,
            CartClient cartClient,
            ProductClient productClient,
            InventoryClient inventoryClient,
            PaymentClient paymentClient
    ) {
        this.orderRepository = orderRepository;
        this.cartClient = cartClient;
        this.productClient = productClient;
        this.inventoryClient = inventoryClient;
        this.paymentClient = paymentClient;
    }

    @Transactional
    public OrderResponse checkout(Long userId, CheckoutRequest request) {

        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User ID must be positive"
            );
        }

        if (request == null || request.paymentMethod() == null
                || request.paymentMethod().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Payment method is required"
            );
        }

        String paymentMethod = request.paymentMethod()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!List.of("UPI", "CARD", "NETBANKING", "COD")
                .contains(paymentMethod)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Unsupported payment method"
            );
        }

        // Payment failure simulation is only for local development.
        List<CartItemResponse> cartItems = cartClient.getCart(userId);

        if (cartItems == null || cartItems.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cart is empty"
            );
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();
        List<ReservedStock> reservedStocks = new ArrayList<>();

        // Calculate the order using prices from Product Service.
        for (CartItemResponse cartItem : cartItems) {

            if (cartItem.quantity() == null || cartItem.quantity() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid quantity for product " + cartItem.productId()
                );
            }

            ProductResponse product =
                    productClient.getProduct(cartItem.productId());

            if (product == null || product.price() == null
                    || product.price().signum() < 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid product or price: " + cartItem.productId()
                );
            }

            BigDecimal unitPrice = product.price();
            BigDecimal subtotal = unitPrice.multiply(
                    BigDecimal.valueOf(cartItem.quantity())
            );

            totalAmount = totalAmount.add(subtotal);

            orderItems.add(new OrderItem(
                    cartItem.productId(),
                    product.name(),
                    cartItem.quantity(),
                    unitPrice,
                    subtotal
            ));
        }

        // Reserve stock. If reservation fails partway through,
        // release only the stock successfully reserved by this attempt.
        try {
            for (CartItemResponse cartItem : cartItems) {
                inventoryClient.reserveStock(
                        cartItem.productId(),
                        cartItem.quantity()
                );

                reservedStocks.add(new ReservedStock(
                        cartItem.productId(),
                        cartItem.quantity()
                ));
            }
        } catch (RuntimeException exception) {
            releaseReservedStock(reservedStocks);
            throw exception;
        }

        // Persist the order before making the remote payment request.
        Order order = new Order(userId, totalAmount);
        order.setStatus(OrderStatus.PENDING_PAYMENT);

        for (OrderItem item : orderItems) {
            order.addItem(item);
        }

        Order savedOrder;

        try {
            savedOrder = orderRepository.saveAndFlush(order);
        } catch (RuntimeException exception) {
            releaseReservedStock(reservedStocks);
            throw exception;
        }

        PaymentClient.PaymentResponse payment;

        try {
            payment = paymentClient.processPayment(
                    savedOrder.getId(),
                    userId,
                    totalAmount,
                    paymentMethod,
                    request.simulateFailure()
            );
        } catch (RuntimeException exception) {
            // Payment outcome may be unknown if the remote call failed.
            // Do not assume payment failed and release stock blindly.
            // A production implementation needs payment-status
            // reconciliation and a durable saga/outbox.
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Could not confirm payment status. Check payment status before retrying."
            );
        }

        if (payment == null || payment.status() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Payment Service returned an invalid response"
            );
        }

        switch (payment.status()) {
            case "SUCCESS" -> {
                savedOrder.setStatus(OrderStatus.CONFIRMED);
                orderRepository.saveAndFlush(savedOrder);

                // Payment succeeded: do not release reserved stock.
                // Cart clearing is best-effort because payment is complete.
                try {
                    cartClient.clearCart(userId);
                } catch (RuntimeException ignored) {
                    // The order is already paid. Do not roll it back
                    // or release stock because cart clearing failed.
                }
            }

            case "COD_PENDING" -> {
                savedOrder.setStatus(
                        OrderStatus.AWAITING_PAYMENT_ON_DELIVERY
                );
                orderRepository.saveAndFlush(savedOrder);

                try {
                    cartClient.clearCart(userId);
                } catch (RuntimeException ignored) {
                    // Preserve the accepted COD order.
                }
            }

            case "FAILED" -> {
                savedOrder.setStatus(OrderStatus.CANCELLED);
                orderRepository.saveAndFlush(savedOrder);

                releaseReservedStock(reservedStocks);
                // Leave the cart intact so the customer can retry.
            }

            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Unexpected payment status: " + payment.status()
            );
        }

        return OrderResponse.from(savedOrder);
    }

    private void releaseReservedStock(List<ReservedStock> reservedStocks) {
        for (int i = reservedStocks.size() - 1; i >= 0; i--) {
            ReservedStock reserved = reservedStocks.get(i);

            try {
                inventoryClient.releaseStock(
                        reserved.productId(),
                        reserved.quantity()
                );
            } catch (RuntimeException ignored) {
                // Production: record failed compensation for retry.
            }
        }
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        return orderRepository
                .findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId)
                .filter(existing -> existing.getUserId().equals(userId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Order not found for this user"
                ));

        return OrderResponse.from(order);
    }

    private record ReservedStock(Long productId, Integer quantity) {
    }
}