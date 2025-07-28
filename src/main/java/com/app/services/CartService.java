package com.app.services;

import com.app.repositories.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * Constructs a new CartService with the required repository.
     *
     * @param cartRepo the repository used to interact with cart data; must not be null
     */
    @Autowired
    public CartService(CartRepository cartRepo) {
        this.cartRepo = cartRepo;
    }

    // Future methods (to be added as needed):
    //
    // public Cart getOrCreateCartForUser(Long userId) { ... }
    // public void clearCart(Long cartId) { ... }
    // public Cart save(Cart cart) { ... }
    // public Optional<Cart> findByUserId(Long userId) { ... }
}