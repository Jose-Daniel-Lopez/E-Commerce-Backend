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
 * Service class for managing shopping cart operations.
 * <p>
 * This service encapsulates the business logic for manipulating {@link Cart} entities,
 * including adding products, updating quantities, and preparing for future enhancements
 * such as cart persistence, user association, promotions, and inventory checks.
 * </p>
 * <p>
 * It acts as an intermediary between the web layer (controllers) and data layer (repositories),
 * ensuring that all cart modifications follow consistent rules and can be extended with
 * validation, logging, or event triggering in the future.
 * </p>
 *
 * @see Cart
 * @see CartRepository
 * @see ProductVariantRepository
 */
@Service
public class CartService {

    /**
     * Repository for performing CRUD operations on {@link Cart} entities.
     * Injected via constructor to ensure immutability and testability.
     */
    private final CartRepository cartRepo;

    /**
     * Repository for accessing {@link ProductVariant} data, used to validate
     * product availability before adding to the cart.
     */
    private final ProductVariantRepository productVariantRepo;

    /**
     * Constructs a {@code CartService} with required dependencies.
     * <p>
     * Dependencies are injected by Spring and guaranteed to be non-null.
     * This design supports dependency inversion and facilitates unit testing.
     * </p>
     *
     * @param cartRepo           the repository for cart data access; must not be {@code null}
     * @param productVariantRepo the repository for product variant data access; must not be {@code null}
     * @throws IllegalArgumentException if either repository is {@code null}
     */
    @Autowired
    public CartService(CartRepository cartRepo, ProductVariantRepository productVariantRepo) {
        this.cartRepo = cartRepo;
        this.productVariantRepo = productVariantRepo;
    }

    /**
     * Adds a product variant to the specified cart. If the product is already in the cart,
     * increments its quantity by one. Otherwise, creates a new cart item with quantity 1.
     * <p>
     * This method enforces referential integrity by validating both the cart and product
     * existence before modification. It does not perform inventory checks; that logic
     * may be added in the future.
     * </p>
     *
     * @param cartId           the unique identifier of the cart; must correspond to an existing cart
     * @param productVariantId the unique identifier of the product variant to add
     * @return the updated {@link Cart} after persisting changes
     * @throws IllegalArgumentException if the cart or product variant does not exist
     * @see CartItem#addQuantity(int)
     * @see Cart#addCartItem(CartItem)
     */
    public Cart addProductToCart(Long cartId, Long productVariantId) {
        // Retrieve the cart; throw exception if not found
        Cart cart = cartRepo.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found with ID: " + cartId));

        // Retrieve the product variant; throw exception if not found
        ProductVariant productVariant = productVariantRepo.findById(productVariantId)
                .orElseThrow(() -> new IllegalArgumentException("Product variant not found with ID: " + productVariantId));

        // Check if the product variant is already in the cart
        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProductVariant().getId().equals(productVariantId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            // Product already in cart: increment quantity
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            // New product: create a cart item and associate it with the cart
            CartItem newCartItem = CartItem.builder()
                    .cart(cart)
                    .productVariant(productVariant)
                    .quantity(1)
                    .build();
            cart.addCartItem(newCartItem); // Use domain method to maintain bidirectional consistency
        }

        // Persist the updated cart and return
        return cartRepo.save(cart);
    }

    // === Future Enhancements (Unimplemented) ===
    // The following methods are planned for future iterations:
    //
    // - getOrCreateCartForUser(Long userId): Ensures a user always has an active cart.
    // - clearCart(Long cartId): Removes all items from a cart (e.g., after checkout).
    // - removeItem(Long cartId, Long productVariantId): Removes a specific item.
    // - calculateTotal(Cart cart): Computes subtotal, tax, and final price.
    // - applyPromotion(String code): Applies discounts if valid.
    //
    // These will be implemented as business requirements evolve.
}