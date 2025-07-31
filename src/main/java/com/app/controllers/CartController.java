package com.app.controllers;

import com.app.entities.Cart;
import com.app.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for managing shopping cart operations via HTTP endpoints.
 * <p>
 * This controller exposes cart-related functionality to external clients (e.g., frontend applications)
 * using a JSON-based API. It delegates all business logic to {@link CartService}, adhering to the
 * principle of separation of concerns.
 * </p>
 * <p>
 * Currently supports adding a product variant to a cart. Designed for future expansion
 * with operations such as retrieving cart details, removing items, or applying discounts.
 * </p>
 * <p>
 * Base URL: {@code /api/cart}
 * </p>
 *
 * @see CartService
 * @see Cart
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    /**
     * Constructs a new {@code CartController} with the required service dependency.
     *
     * @param cartService the service responsible for cart business logic; must not be {@code null}
     * @throws IllegalArgumentException if {@code cartService} is {@code null}
     */
    @Autowired
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * Adds a product variant to the specified cart.
     * <p>
     * HTTP POST endpoint that triggers the addition of a product (via its variant ID)
     * to an existing cart. If the product is already in the cart, its quantity is incremented.
     * Otherwise, a new cart item is created.
     * </p>
     * <p>
     * Example request:
     * <pre>
     * POST /api/cart/123/products/456
     * </pre>
     * </p>
     * <p>
     * On success, returns the updated cart in JSON format. If the cart or product variant
     * does not exist, a {@code 400 Bad Request} is returned (handled by global exception handlers).
     * </p>
     *
     * @param cartId           the unique identifier of the cart; must correspond to an existing cart
     * @param productVariantId the unique identifier of the product variant to add
     * @return the updated {@link Cart} object, serialized as JSON
     *
     * @throws IllegalArgumentException if the cart or product variant is not found
     *         (typically mapped to HTTP 400 via {@code @ControllerAdvice})
     *
     * @apiNote This method is state-changing (POST) and should not be idempotent.
     *          Consider using PATCH or PUT semantics in the future for more granular updates.
     */
    @PostMapping("/{cartId}/products/{productVariantId}")
    public Cart addProductToCart(
            @PathVariable Long cartId,
            @PathVariable Long productVariantId) {
        return cartService.addProductToCart(cartId, productVariantId);
    }

    // === Planned Endpoints (Future Work) ===
    //
    // @GetMapping("/{cartId}")
    //   - Retrieve current cart state
    //
    // @DeleteMapping("/{cartId}/items/{productVariantId}")
    //   - Remove a specific item
    //
    // @PutMapping("/{cartId}/items/{productVariantId}")
    //   - Update quantity directly
    //
    // @GetMapping("/user/{userId}")
    //   - Get cart by user (requires findByUserId in service)
    //
    // These will be added as the frontend and business logic evolve.
}