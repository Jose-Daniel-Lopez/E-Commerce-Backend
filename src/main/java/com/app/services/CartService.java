package com.app.services;

import com.app.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class CartService {
    
    // Repository for Cart entity
    private final CartRepository cartRepo;

    // Constructor-based dependency injection for CartRepository
    @Autowired
    public CartService(CartRepository cartRepo) {
        this.cartRepo = cartRepo;
    }

}
