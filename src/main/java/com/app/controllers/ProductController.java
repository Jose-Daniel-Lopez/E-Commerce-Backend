package com.app.controllers;

import com.app.DTO.NewlyAddedProductDTO;
import com.app.DTO.ProductByCategorySummaryDTO;
import com.app.entities.Product;
import com.app.hateoas.ProductRepresentation;
import com.app.hateoas.HateoasLinkBuilder;
import com.app.repositories.ProductRepository;
import com.app.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // Injecting ProductService to handle product-related operations
    private final ProductService productService;
    private final ProductRepository productRepo;
    private final HateoasLinkBuilder hateoasLinkBuilder;

    // Constructor injection for ProductService
    @Autowired
    public ProductController(ProductService productService, ProductRepository productRepo, HateoasLinkBuilder hateoasLinkBuilder) {
        this.productService = productService;
        this.productRepo = productRepo;
        this.hateoasLinkBuilder = hateoasLinkBuilder;
    }

    // Get product by ID with HATEOAS links
    @GetMapping("/{id}")
    public ResponseEntity<ProductRepresentation> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        ProductRepresentation productRepresentation = hateoasLinkBuilder.buildProductRepresentation(product);
        return ResponseEntity.ok(productRepresentation);
    }

    // Get products by category with pagination
    @GetMapping("/category/{categoryId}")
    public Page<Product> getProductsByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return productService.getProductsByCategory(categoryId, pageable);
    }

    // Endpoint to get products with optional featured filter
    @GetMapping("/featured")
    public Page<Product> getFeaturedProducts(Pageable pageable) {
        return productRepo.findByIsFeatured(true, pageable);
    }

    @GetMapping("/brands")
    public List<String> getAllBrands() {
        return productService.getAllBrands();
    }

    @GetMapping("/memories")
    public List<String> getAllMemoryOptions() {
        return productService.getAllMemoryOptions();
    }

    @GetMapping("/new")
    public List<NewlyAddedProductDTO> getNewlyCreatedProducts() {
        return productService.getNewlyCreatedProducts().stream()
                .map(p -> new NewlyAddedProductDTO(p.getImageUrl(), p.getName(), p.getBasePrice()))
                .toList();
    }

    @GetMapping("/category")
    public List<ProductByCategorySummaryDTO> getProductSummariesByCategory(@RequestParam String name) {
        return productService.getProductSummariesByCategory(name);
    }
}
