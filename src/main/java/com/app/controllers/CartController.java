package com.app.controllers;

import com.app.entities.Cart;
import com.app.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/{cartId}/products/{productVariantId}")
    public Cart addProductToCart(
            @PathVariable Long cartId,
            @PathVariable Long productVariantId) {
        return cartService.addProductToCart(cartId, productVariantId);
    }
}
