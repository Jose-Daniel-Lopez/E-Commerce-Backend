package com.app.repositories;

import com.app.entities.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    // Additional query methods can be defined here if needed

    // Example: Find reviews by rating
    // List<ProductReview> findByRating(Integer rating);

    // Example: Find reviews by product ID (assuming a productId field exists)
    // List<ProductReview> findByProductId(Long productId);
}
