package com.app.repositories;

import com.app.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    // Custom query methods can be defined here if needed
    // For example, find by user ID or title
    List<Wishlist> findByUserId(Long userId);

    Optional<Wishlist> findByTitleAndUserId(String title, Long userId);
}
