package com.app.controllers;

import com.app.entities.Product;
import com.app.repositories.ProductRepository;
import com.app.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Scanner;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // Injecting ProductService to handle product-related operations
    private final ProductService productService;
    private final ProductRepository productRepo;

    // Constructor injection for ProductService
    @Autowired
    public ProductController(ProductService productService, ProductRepository productRepo) {
        this.productService = productService;
        this.productRepo = productRepo;
    }

    // Endpoint to get products with optional featured filter
    @GetMapping("/featured")
    public Page<Product> getProducts(
            @RequestParam(required = false) Boolean featured,
            Pageable pageable
    ) {
        if (featured != null) {
            return productRepo.findByIsFeatured(featured, pageable);
        }
        return productRepo.findAll(pageable);
    }
}
