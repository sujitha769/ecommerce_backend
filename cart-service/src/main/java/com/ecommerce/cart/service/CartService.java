package com.ecommerce.cart.service;

import com.ecommerce.cart.client.InventoryClient;
import com.ecommerce.cart.client.ProductClient;
import com.ecommerce.cart.dto.AddToCartRequest;
import com.ecommerce.cart.dto.CartItemResponse;
import com.ecommerce.cart.dto.UpdateCartItemRequest;
import com.ecommerce.cart.entity.CartItem;
import com.ecommerce.cart.repository.CartItemRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;
    private final InventoryClient inventoryClient;

    public CartService(
            CartItemRepository cartItemRepository,
            ProductClient productClient,
            InventoryClient inventoryClient) {

        this.cartItemRepository = cartItemRepository;
        this.productClient = productClient;
        this.inventoryClient = inventoryClient;
    }

    @Transactional
    public CartItemResponse addItem(
            Long userId,
            AddToCartRequest request) {

        validateUserId(userId);

        productClient.verifyProductExists(request.productId());

        CartItem item = cartItemRepository
                .findByUserIdAndProductId(userId, request.productId())
                .orElse(null);

        long requestedTotal = request.quantity();

        if (item != null) {
            requestedTotal += item.getQuantity();
        }

        if (requestedTotal > Integer.MAX_VALUE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Requested quantity is too large"
            );
        }

        int available = inventoryClient.getAvailableQuantity(
                request.productId()
        );

        if (requestedTotal > available) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Requested quantity exceeds available stock. Available: "
                            + available
            );
        }

        if (item == null) {
            item = new CartItem();
            item.setUserId(userId);
            item.setProductId(request.productId());
        }

        item.setQuantity((int) requestedTotal);

        return CartItemResponse.from(cartItemRepository.save(item));
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(Long userId) {
        validateUserId(userId);

        return cartItemRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(CartItemResponse::from)
                .toList();
    }

    @Transactional
    public CartItemResponse updateItem(
            Long userId,
            Long itemId,
            UpdateCartItemRequest request) {

        validateUserId(userId);

        CartItem item = findCartItem(userId, itemId);

        int available = inventoryClient.getAvailableQuantity(
                item.getProductId()
        );

        if (request.quantity() > available) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Requested quantity exceeds available stock. Available: "
                            + available
            );
        }

        item.setQuantity(request.quantity());

        return CartItemResponse.from(cartItemRepository.save(item));
    }

    @Transactional
    public void removeItem(Long userId, Long itemId) {
        validateUserId(userId);

        CartItem item = findCartItem(userId, itemId);
        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Long userId) {
        validateUserId(userId);
        cartItemRepository.deleteByUserId(userId);
    }

    private CartItem findCartItem(Long userId, Long itemId) {
        return cartItemRepository.findByIdAndUserId(itemId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Cart item not found: " + itemId
                ));
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User ID must be positive"
            );
        }
    }
}