package com.app.repositories;

import com.app.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {


    // Additional custom query methods can be defined here if needed

    // Example: Find CartItem by Cart ID
    // Optional<CartItem> findByCartId(Long cartId);

    // Example: Delete CartItem by Cart ID
    // void deleteByCartId(Long cartId);
    long countByCartId(Long id);
}
