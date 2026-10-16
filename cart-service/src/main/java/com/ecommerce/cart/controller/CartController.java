
        package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartItemResponse;
import com.ecommerce.cart.dto.UpdateCartItemRequest;
import com.ecommerce.cart.service.CartService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Add a product to the user's cart
    @PostMapping("/{userId}/items")
    public ResponseEntity<CartItemResponse> addItem(
            @PathVariable("userId") Long userId,
            @Valid @RequestBody AddToCartRequest request) {

        CartItemResponse response =
                cartService.addItem(userId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all items in the user's cart
    @GetMapping("/{userId}")
    public ResponseEntity<List<CartItemResponse>> getCart(
            @PathVariable("userId") Long userId) {

        return ResponseEntity.ok(
                cartService.getCart(userId)
        );
    }

    // Update the quantity of a cart item
    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartItemResponse> updateItem(
            @PathVariable("userId") Long userId,
            @PathVariable("itemId") Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {

        return ResponseEntity.ok(
                cartService.updateItem(userId, itemId, request)
        );
    }

    // Remove one item from the cart
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable("userId") Long userId,
            @PathVariable("itemId") Long itemId) {

        cartService.removeItem(userId, itemId);

        return ResponseEntity.noContent().build();
    }

    // Remove all items from the cart
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(
            @PathVariable("userId") Long userId) {

        cartService.clearCart(userId);

        return ResponseEntity.noContent().build();
    }
}
