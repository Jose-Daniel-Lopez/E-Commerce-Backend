package com.app.repositories;

import com.app.entities.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for managing {@link ProductReview} entities.
 * <p>
 * Extends {@link JpaRepository} to provide CRUD operations out-of-the-box.
 * Additional custom query methods can be defined here using method naming conventions
 * or {@link Query @Query} annotations for complex queries.
 * </p>
 *
 * <p>
 * This repository is automatically implemented by Spring Data JPA at runtime.
 * It is annotated with {@link Repository @Repository} to indicate its role in the persistence layer.
 * </p>
 */
@Repository
public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {

    /**
     * Finds all reviews associated with a specific product.
     *
     * @param productId the ID of the product
     * @return a list of reviews for the given product
     */
    List<ProductReview> findByProduct_Id(Long productId);

    /**
     * Finds all reviews associated with a specific user.
     *
     * @param userId the ID of the user
     * @return a list of reviews created by the given user
     */
    List<ProductReview> findByUser_Id(Long userId);

    /**
     * Finds all reviews with a specific rating.
     *
     * @param rating the rating value (e.g., 5 for top-rated)
     * @return a list of reviews matching the given rating
     */
    List<ProductReview> findByRating(Integer rating);

    /**
     * Finds all reviews for a given product, sorted and paginated.
     *
     * @param productId the ID of the product
     * @param pageable  pagination and sorting information
     * @return a paged result of reviews
     */
    Page<ProductReview> findByProduct_Id(Long productId, Pageable pageable);

    /**
     * Computes the average rating for a specific product.
     *
     * @param productId the ID of the product
     * @return the average rating as a Double (can be null if no reviews exist)
     */
    @Query("SELECT AVG(pr.rating) FROM ProductReview pr WHERE pr.product.id = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
}