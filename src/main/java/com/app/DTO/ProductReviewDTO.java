package com.app.DTO;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) representing a product review submitted by a user.
 * <p>
 * This class is used to transfer product review data between layers (e.g., controller, service)
 * and includes validation constraints to ensure data integrity.
 * </p>
 */
@Getter
@Setter
public class ProductReviewDTO {

    /**
     * Unique identifier of the product review.
     * May be null when creating a new review (assigned by the system on persistence).
     */
    private Long id;

    /**
     * Rating given by the user for the product.
     * Must be between 1 (lowest) and 5 (highest).
     *
     * @required Yes
     * @range 1 - 5
     */
    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating must be at most 5")
    private Integer rating;

    /**
     * Optional comment provided by the user.
     * Maximum length is 1000 characters.
     */
    @Size(max = 1000, message = "Comment cannot exceed 1000 characters")
    private String comment;

    /**
     * Identifier of the product being reviewed.
     *
     * @required Yes
     */
    @NotNull(message = "Product ID is required")
    private Long productId;

    /**
     * Identifier of the user who submitted the review.
     *
     * @required Yes
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * Timestamp when the review was created.
     * Automatically set by the system upon creation.
     */
    private LocalDateTime createdAt;

    // Note: Lombok handles all getters and setters.
    // No additional methods or logic are needed in this DTO.
}