package com.app.controllers;

import com.app.DTO.CartItemQuantityDTO;
import com.app.entities.CartItem;
import com.app.services.CartItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.NoSuchElementException;

/**
 * REST controller for managing cart items.
 * <p>
 * Provides endpoints to retrieve, update, and delete individual cart items.
 * Designed to be used in conjunction with the shopping cart system.
 * </p>
 * <p>
 * Base URL: {@code /api/cartItems}
 * </p>
 */
@RestController
@RequestMapping("/api/cartItems")
public class CartItemController {

    private final CartItemService cartItemService;

    /**
     * Constructs a new CartItemController with the required service dependency.
     *
     * @param cartItemService the service responsible for cart item business logic
     */
    @Autowired
    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

    /**
     * Retrieves a specific cart item by its ID.
     *
     * @param cartItemId the unique identifier of the cart item
     * @return {@link ResponseEntity} with the cart item if found; 404 if not found
     * @response 200 Successfully returns the cart item
     * @response 404 Cart item not found
     */
    @GetMapping("/{cartItemId}")
    public ResponseEntity<CartItem> getCartItemById(@PathVariable Long cartItemId) {
        CartItem cartItem = cartItemService.findById(cartItemId);
        return cartItem != null
                ? ResponseEntity.ok(cartItem)
                : ResponseEntity.notFound().build();
    }

    /**
     * Updates the quantity of a specific cart item.
     * <p>
     * A quantity of 0 or less may be interpreted as a request to remove the item.
     * The actual behavior is determined by the service implementation.
     * </p>
     *
     * @param cartItemId   the unique identifier of the cart item to update
     * @param quantityDTO  the new quantity wrapped in a DTO
     * @return {@link ResponseEntity} with the updated cart item if successful; 404 if not found
     * @response 200 Successfully returns the updated cart item
     * @response 400 Invalid quantity (e.g., negative, null)
     * @response 404 Cart item not found
     */
    @PatchMapping("/{cartItemId}")
    public ResponseEntity<CartItem> updateCartItemQuantity(
            @PathVariable Long cartItemId,
            @RequestBody CartItemQuantityDTO quantityDTO) {

        try {
            CartItem updatedCartItem = cartItemService.updateQuantity(cartItemId, quantityDTO.getQuantity());
            return ResponseEntity.ok(updatedCartItem);
        } catch (IllegalArgumentException e) {
            // Quantity is invalid (e.g., negative)
            return ResponseEntity.badRequest().build();
        } catch (RuntimeException e) {
            // Item not found or other runtime issue
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Removes a cart item from the cart.
     *
     * @param cartItemId the unique identifier of the cart item to delete
     * @return {@link ResponseEntity} with no content on success; 404 if not found
     * @response 204 Successfully deleted
     * @response 404 Cart item not found
     */
    @DeleteMapping("/{cartItemId}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        try {
            cartItemService.deleteCartItem(cartItemId);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}