package com.app.repositories;

import com.app.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {

    /**
     * Custom method to find a cart by user ID.
     * This can be useful for retrieving a user's cart directly.
     *
     * @param userId the ID of the user whose cart is to be retrieved
     * @return an Optional containing the Cart if found, or empty if not found
     */
    Optional<Cart> findByUserId(Long userId);
}
