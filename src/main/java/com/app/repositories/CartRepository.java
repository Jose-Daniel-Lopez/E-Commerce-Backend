package com.app.repositories;

import com.app.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing {@link Cart} entities in the persistence layer.
 * <p>
 * This interface extends {@link JpaRepository}, providing built-in CRUD operations
 * and allowing the definition of custom query methods using Spring Data JPA conventions.
 * </p>
 * <p>
 * It serves as a data access contract for services (e.g., {@link com.app.services.CartService})
 * to retrieve, persist, and query cart data without coupling to implementation details.
 * </p>
 *
 * @see Cart
 * @see com.app.services.CartService
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Retrieves the cart associated with the given user ID.
     * <p>
     * This method searches for a {@link Cart} where the {@code userId} field matches
     * the provided identifier. Since a user typically has at most one active cart,
     * this method returns an {@link Optional} to safely handle cases where no cart exists.
     * </p>
     * <p>
     * The query is derived automatically by Spring Data JPA from the method name.
     * No implementation is required.
     * </p>
     *
     * @param userId the unique identifier of the user whose cart is being retrieved;
     *               must not be null (though behavior depends on database constraints)
     * @return an {@link Optional} containing the found {@link Cart}, or {@link Optional#empty()}
     *         if no cart is associated with the given user ID
     *
     * @implSpec This method uses a derived query based on the {@code userId} property
     *           of the {@link Cart} entity. Ensure this field is properly mapped and indexed
     *           for performance in production environments.
     */
    Optional<Cart> findByUserId(Long userId);

    // Future query examples (unimplemented):
    //
    // Optional<Cart> findBySessionId(String sessionId);
    // List<Cart> findByStatusAndCreatedAtBefore(CartStatus status, LocalDateTime cutoff);
    //
    // These can be added as business logic evolves (e.g., guest carts, abandoned cart cleanup).
}