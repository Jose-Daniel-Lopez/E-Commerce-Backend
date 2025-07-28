package com.app.services;

import com.app.entities.CartItem;
import com.app.repositories.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing business logic related to {@link CartItem} entities.
 * <p>
 * This service acts as an intermediary between the web layer and the repository,
 * encapsulating operations such as validation, retrieval, and modification of cart items.
 * It ensures that business rules (e.g., quantity validity) are enforced before data is persisted.
 * </p>
 * <p>
 * Currently supports:
 * <ul>
 *   <li>Finding a cart item by ID</li>
 *   <li>Updating the quantity of a cart item</li>
 *   <li>Deleting a cart item</li>
 * </ul>
 * </p>
 */
@Service
public class CartItemService {

    // Repository for data access operations on CartItem; initialized via constructor injection
    private final CartItemRepository cartItemRepo;

    /**
     * Constructs a new CartItemService with the required repository.
     *
     * @param cartItemRepo the repository used to persist and retrieve cart items; must not be null
     */
    @Autowired
    public CartItemService(CartItemRepository cartItemRepo) {
        this.cartItemRepo = cartItemRepo;
    }

    /**
     * Finds and returns a cart item by its ID.
     *
     * @param cartItemId the unique identifier of the cart item; must not be null
     * @return the found {@link CartItem} instance
     * @throws RuntimeException if no cart item exists with the given ID
     */
    public CartItem findById(Long cartItemId) {
        return cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));
    }

    /**
     * Updates the quantity of a specified cart item.
     * <p>
     * Quantity must be greater than zero. If not, an exception is thrown.
     * After updating, the modified cart item is saved and returned.
     * </p>
     *
     * @param cartItemId  the ID of the cart item to update; must correspond to an existing item
     * @param newQuantity the new quantity to set; must be greater than 0
     * @return the updated {@link CartItem}
     * @throws IllegalArgumentException if newQuantity is less than or equal to 0
     * @throws RuntimeException         if no cart item exists with the given ID
     */
    public CartItem updateQuantity(Long cartItemId, Integer newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        CartItem cartItem = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));

        cartItem.setQuantity(newQuantity);
        return cartItemRepo.save(cartItem);
    }

    /**
     * Deletes a cart item by its ID.
     * <p>
     * The item must exist; otherwise, an exception is thrown.
     * </p>
     *
     * @param cartItemId the ID of the cart item to delete; must correspond to an existing item
     * @throws RuntimeException if no cart item exists with the given ID
     */
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));

        cartItemRepo.delete(cartItem);
    }
}