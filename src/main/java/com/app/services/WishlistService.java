package com.app.services;

import com.app.repositories.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepo;

    @Autowired
    public WishlistService(WishlistRepository wishlistRepo) {
        this.wishlistRepo = wishlistRepo;
    }

    // Add methods to handle business logic related to Wishlists
    // For example, methods to create, update, delete, and retrieve wishlists
    // ...

}
