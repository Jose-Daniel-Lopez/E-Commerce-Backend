package com.app.services;

import com.app.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    // Service class for managing products
    private final ProductRepository productRepo;

    // Constructor injection for ProductRepository
    @Autowired
    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }
}
