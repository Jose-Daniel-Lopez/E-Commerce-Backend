package com.app.controllers;

import com.app.DTO.ProductReviewDTO;
import com.app.entities.ProductReview;
import com.app.services.ProductReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * REST controller for managing product reviews.
 * <p>
 * Provides endpoints to create and, in the future, retrieve, update, or delete product reviews.
 * Currently supports creating a new review via POST request.
 * </p>
 *
 * @author YourTeamName (optional)
 * @since 1.0
 */
@RestController
@RequestMapping(path = "/api/reviews", produces = APPLICATION_JSON_VALUE)
public class ProductReviewController {

    private final ProductReviewService productReviewService;

    /**
     * Constructs a new ProductReviewController with the required service dependency.
     *
     * @param productReviewService the service responsible for business logic of product reviews
     *                             (must not be null)
     */
    public ProductReviewController(ProductReviewService productReviewService) {
        this.productReviewService = productReviewService;
    }

    /**
     * Creates a new product review based on the provided data.
     * <p>
     * Validates the incoming request body using Jakarta Bean Validation. If validation fails,
     * a {@link org.springframework.web.bind.MethodArgumentNotValidException} will be thrown
     * and handled by Spring's global exception handling mechanism.
     * </p>
     *
     * @param reviewDTO the data transfer object containing review details (rating, comment, etc.)
     * @return {@link ResponseEntity} containing the created {@link ProductReview} with status 200 (OK)
     */
    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<ProductReview> createReview(@Valid @RequestBody ProductReviewDTO reviewDTO) {
        ProductReview createdReview = productReviewService.createReview(reviewDTO);
        return ResponseEntity.ok(createdReview);
    }
}