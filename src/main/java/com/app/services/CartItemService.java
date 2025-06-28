package com.app.services;

import com.app.entities.CartItem;
import com.app.repositories.CartItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartItemService {

// Service class for handling business logic related to CartItem entities
    private final CartItemRepository cartItemRepo;

    // Constructor-based dependency injection for CartItemRepository
    @Autowired
    public CartItemService(CartItemRepository cartItemRepo) {
        this.cartItemRepo = cartItemRepo;
    }

    // Method to delete a CartItem by its ID
    public void deleteCartItem(Long cartItemId) {
        CartItem cartItem = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));

        cartItemRepo.delete(cartItem);
    }

    // Method to update the quantity of a CartItem
    public CartItem updateQuantity(Long cartItemId, Integer newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        CartItem cartItem = cartItemRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found with id: " + cartItemId));

        cartItem.setQuantity(newQuantity);
        return cartItemRepo.save(cartItem);
    }
}
