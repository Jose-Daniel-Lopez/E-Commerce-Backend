package com.app.data.seeder;

import com.app.entities.Cart;
import com.app.entities.CartItem;
import com.app.entities.ProductVariant;
import com.app.repositories.CartItemRepository;
import com.app.repositories.CartRepository;
import com.app.repositories.ProductVariantRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CartItemSeeder {

    private final CartItemRepository cartItemRepo;
    private final CartRepository cartRepo;
    private final ProductVariantRepository productVariantRepo;
    private final Random random = new Random();

    public CartItemSeeder(CartItemRepository cartItemRepo, CartRepository cartRepo, ProductVariantRepository productVariantRepo) {
        this.cartItemRepo = cartItemRepo;
        this.cartRepo = cartRepo;
        this.productVariantRepo = productVariantRepo;
    }

    public void seed() {
        if (cartItemRepo.count() > 0) return;
        System.out.println("Seeding cart items...");
        List<Cart> carts = cartRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();
        if (carts.isEmpty() || variants.isEmpty()) return;

        List<CartItem> cartItems = new ArrayList<>();

        for (Cart cart : carts) {
            // 50% chance a cart has items
            if (random.nextBoolean()) {
                int itemCount = random.nextInt(3) + 1; // 1-3 items per cart

                for (int i = 0; i < itemCount; i++) {
                    ProductVariant variant = variants.get(random.nextInt(variants.size()));
                    int quantity = random.nextInt(2) + 1; // 1-2 quantity

                    CartItem cartItem = CartItem.builder()
                            .cart(cart)
                            .productVariant(variant)
                            .quantity(quantity)
                            .build();
                    cartItems.add(cartItem);
                }
            }
        }

        cartItemRepo.saveAll(cartItems);
        System.out.println(cartItems.size() + " cart items created.");
    }
}

