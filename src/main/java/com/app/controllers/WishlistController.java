package com.app.controllers;

import com.app.entities.Wishlist;
import com.app.services.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlists")
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

    @DeleteMapping("/{wishlistId}/products/{productId}")
    public Wishlist removeProductFromWishlist(
            @PathVariable Long wishlistId,
            @PathVariable Long productId) {
        return wishlistService.removeProductFromWishlist(wishlistId, productId);
    }
}
