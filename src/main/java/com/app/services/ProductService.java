package com.app.services;

import com.app.entities.Product;
import com.app.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
