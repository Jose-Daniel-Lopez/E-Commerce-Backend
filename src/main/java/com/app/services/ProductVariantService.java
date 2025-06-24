package com.app.services;

import com.app.repositories.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductVariantService {

    // Injecting the ProductVariantRepository to handle database operations
    private final ProductVariantRepository productVariantRepo;

    // Constructor injection for ProductVariantRepository
    @Autowired
    public ProductVariantService(ProductVariantRepository productVariantRepo) {
        this.productVariantRepo = productVariantRepo;
    }
}
