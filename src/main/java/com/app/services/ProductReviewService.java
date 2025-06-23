package com.app.services;

import com.app.repositories.ProductReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductReviewService {

    // This service class will handle business logic related to Product Reviews
    private final ProductReviewRepository productReviewRepo;

    // Constructor-based dependency injection for ProductReviewRepository
    @Autowired
    public ProductReviewService(ProductReviewRepository productReviewRepo) {
        this.productReviewRepo = productReviewRepo;
    }
}
