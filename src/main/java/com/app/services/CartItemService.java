package com.app.services;

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
}
