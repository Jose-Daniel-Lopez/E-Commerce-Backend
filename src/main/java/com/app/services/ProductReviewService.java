package com.app.services;

import com.app.DTO.ProductReviewDTO;
import com.app.entities.Product;
import com.app.entities.ProductReview;
import com.app.entities.User;
import com.app.repositories.ProductRepository;
import com.app.repositories.ProductReviewRepository;
import com.app.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Service class responsible for managing the business logic of product reviews.
 * <p>
 * This service acts as the intermediary between the {@link com.app.controllers.ProductReviewController}
 * and the data layer (repositories), enforcing domain rules such as:
 * </p>
 * <ul>
 *   <li>Validating the existence of referenced products and users</li>
 *   <li>Preventing reviews for non-existent or inactive entities</li>
 *   <li>Setting default timestamps if not provided</li>
 *   <li>Serving as a foundation for future rules (e.g., purchase verification, duplicate prevention)</li>
 * </ul>
 * <p>
 * All persistence operations are delegated to Spring Data JPA repositories.
 * This class is designed for future extension with validation, moderation, rating aggregation, and audit features.
 * </p>
 *
 * @author YourTeamName (optional)
 * @since 1.0
 */
@Service
@Transactional
public class ProductReviewService {

    private final ProductReviewRepository productReviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Constructs a new ProductReviewService with required dependencies.
     * <p>
     * Uses constructor injection for reliable dependency management and testability.
     * No {@code @Autowired} annotation is needed in Spring Boot when only one constructor is present.
     * </p>
     *
     * @param productReviewRepository the repository for managing {@link ProductReview} entities;
     *                                must not be {@code null}
     * @param productRepository       the repository for managing {@link Product} entities;
     *                                must not be {@code null}
     * @param userRepository          the repository for managing {@link User} entities;
     *                                must not be {@code null}
     */
    public ProductReviewService(
            ProductReviewRepository productReviewRepository,
            ProductRepository productRepository,
            UserRepository userRepository) {
        this.productReviewRepository = productReviewRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    /**
     * Creates and persists a new product review.
     * <p>
     * Validates that the referenced product and user exist in the system before creating the review.
     * If either is not found, an {@link IllegalArgumentException} is thrown.
     * </p>
     * <p>
     * The creation timestamp is set to the value in the DTO, or defaults to the current time
     * if not provided.
     * </p>
     *
     * @param dto the data transfer object containing review details; must not be {@code null}
     * @return the saved {@link ProductReview} entity with generated ID and relationships
     * @throws IllegalArgumentException if the product or user does not exist
     */
    @Transactional
    public ProductReview createReview(ProductReviewDTO dto) {
        // Validate product existence
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + dto.getProductId()));

        // Validate user existence
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + dto.getUserId()));

        // Build and persist the review
        ProductReview review = ProductReview.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : java.time.LocalDateTime.now())
                .product(product)
                .user(user)
                .build();

        return productReviewRepository.save(review);
    }

    // ========================================================================
    // Future Extension Points (Uncomment and implement as needed)
    // ========================================================================

    /*
     * Updates an existing review's comment and/or rating.
     *
    public ProductReview updateReview(Long reviewId, ProductReviewDTO dto) {
        ProductReview existingReview = productReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!existingReview.getUser().getId().equals(dto.getUserId())) {
            throw new AccessDeniedException("Cannot edit another user's review");
        }

        existingReview.setRating(dto.getRating());
        existingReview.setComment(dto.getComment());
        return productReviewRepository.save(existingReview);
    }
    */

    /*
     * Deletes a review by its ID.
     *
    public void deleteReview(Long reviewId) {
        if (!productReviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review not found");
        }
        productReviewRepository.deleteById(reviewId);
    }
    */

    /*
     * Checks if a user has already reviewed a specific product (to prevent duplicates).
     *
    public boolean hasUserReviewedProduct(Long userId, Long productId) {
        return productReviewRepository.existsByUserIdAndProductId(userId, productId);
    }
    */

    /*
     * Calculates the average rating for a given product.
     *
    @Transactional(readOnly = true)
    public Double getAverageRatingForProduct(Long productId) {
        return productReviewRepository.findAverageRatingByProductId(productId);
    }
    */
}