package com.app.services;

import com.app.entities.Product;
import com.app.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProductService {

    // Service class for managing products
    private final ProductRepository productRepo;

    // Constructor injection for ProductRepository
    @Autowired
    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    // Return all products
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    // Get product by ID
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    // Get products by category with pagination
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepo.findByCategoryId(categoryId, pageable);
    }

    // Return all brands
    public List<String> getAllBrands() {
        return productRepo.findAllDistinctBrands();
    }

    private static final List<String> MEMORY_OPTIONS = List.of(
            "32GB", "64GB", "128GB", "256GB", "512GB", "1TB", "2TB", "4TB", "8TB"
    );

    // Return all memory options
    public List<String> getAllMemoryOptions() {
        return MEMORY_OPTIONS;
    }

    // Get products added in the last 7 days
    public List<Product> getNewlyCreatedProducts() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7);
        return productRepo.findNewlyCreatedBetween(startDate, endDate);
    }
}
