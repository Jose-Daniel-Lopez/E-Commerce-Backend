package com.app.services;

import com.app.entities.Cart;
import com.app.entities.CartItem;
import com.app.entities.ProductVariant;
import com.app.repositories.CartRepository;
import com.app.repositories.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service class responsible for handling business logic related to {@code Cart} entities.
 * <p>
 * This service serves as a bridge between the controller and repository layers,
 * ensuring that operations on shopping carts go through a centralized layer
 * where business rules (e.g., cart limits, merge logic, session handling)
 * can be implemented in the future.
 * </p>
 * <p>
 * Currently, this class holds a dependency on {@link CartRepository}
 * and is prepared for methods such as:
 * </p>
 * <ul>
 *   <li>Retrieving a user's active cart</li>
 *   <li>Creating or initializing a new cart</li>
 *   <li>Clearing or closing a cart</li>
 *   <li>Applying promotions or calculating totals (if logic resides here)</li>
 * </ul>
 * <p>
 * All data persistence operations are delegated to the injected repository.
 * </p>
 */
@Service
public class CartService {

    // Repository for data access operations related to Cart; initialized via constructor injection
    private final CartRepository cartRepo;
    private final ProductVariantRepository productVariantRepo;

    /**
     * Constructs a new CartService with the required repositories.
     *
     * @param cartRepo the repository used to interact with cart data; must not be null
     * @param productVariantRepo the repository used to interact with product variant data; must not be null
     */
    @Autowired
    public CartService(CartRepository cartRepo, ProductVariantRepository productVariantRepo) {
        this.cartRepo = cartRepo;
        this.productVariantRepo = productVariantRepo;
    }

    public Cart addProductToCart(Long cartId, Long productVariantId) {
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        ProductVariant productVariant = productVariantRepo.findById(productVariantId)
                .orElseThrow(() -> new IllegalArgumentException("Product variant not found"));

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(productVariantId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(productVariant)
                    .quantity(1)
                    .build();
            cart.addCartItem(newCartItem);
        }
        return cartRepo.save(cart);
    }
    // Future methods (to be added as needed):
    //
    // public Cart getOrCreateCartForUser(Long userId) { ... }
    // public void clearCart(Long cartId) { ... }
    // public Cart save(Cart cart) { ... }
    // public Optional<Cart> findByUserId(Long userId) { ... }
}