package com.app.controllers;

import com.app.entities.Wishlist;
import com.app.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    @Autowired
    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @PostMapping("/{wishlistId}/products/{productId}")
    public Wishlist addProductToWishlist(
            @PathVariable Long wishlistId,
            @PathVariable Long productId) {
        return wishlistService.addProductToWishlist(wishlistId, productId);
    }
}
