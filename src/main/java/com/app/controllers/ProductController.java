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

/**
 * REST controller for managing product-related endpoints.
 * Exposes HTTP APIs for retrieving product data with support for filtering,
 * pagination, HATEOAS, and lightweight DTO projections.
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepo;
    private final HateoasLinkBuilder hateoasLinkBuilder;

    /**
     * Constructs a ProductController with required dependencies.
     * Uses constructor injection for ProductService, ProductRepository, and HateoasLinkBuilder.
     *
     * @param productService the service layer for business logic
     * @param productRepo the repository for direct database operations
     * @param hateoasLinkBuilder builds HATEOAS-compliant representations with navigational links
     */
    @Autowired
    public ProductController(
            ProductService productService,
            ProductRepository productRepo,
            HateoasLinkBuilder hateoasLinkBuilder) {
        this.productService = productService;
        this.productRepo = productRepo;
        this.hateoasLinkBuilder = hateoasLinkBuilder;
    }

    /**
     * Retrieves a product by its ID and enhances it with HATEOAS links.
     * Returns a {@link ProductRepresentation} that includes self-referential and related resource links.
     *
     * @param id the unique identifier of the product
     * @return ResponseEntity containing the HATEOAS-enabled product representation if found;
     *         otherwise, returns 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductRepresentation> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        ProductRepresentation productRepresentation = hateoasLinkBuilder.buildProductRepresentation(product);
        return ResponseEntity.ok(productRepresentation);
    }

    /**
     * Retrieves a paginated list of products belonging to a specific category.
     * Supports pagination and sorting via {@link Pageable}.
     *
     * @param categoryId the ID of the category to filter products by
     * @param pageable pagination and sorting parameters (e.g., page, size, sort)
     * @return a Page of Product entities matching the category
     */
    @GetMapping("/category/{categoryId}")
    public Page<Product> getProductsByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return productService.getProductsByCategory(categoryId, pageable);
    }

    /**
     * Retrieves a paginated list of featured products (e.g., highlighted items on the homepage).
     *
     * @param pageable pagination and sorting parameters
     * @return a Page of Product entities where isFeatured = true
     */
    @GetMapping("/featured")
    public Page<Product> getFeaturedProducts(Pageable pageable) {
        return productRepo.findByIsFeatured(true, pageable);
    }

    /**
     * Retrieves a list of all distinct brand names available in the product catalog.
     * Used for populating brand filters in the UI.
     *
     * @return a List of unique brand names as strings
     */
    @GetMapping("/brands")
    public List<String> getAllBrands() {
        return productService.getAllBrands();
    }

    /**
     * Retrieves a list of standard memory options supported by the system.
     * These are predefined values (not dynamically fetched from products).
     *
     * @return a List of memory capacity strings (e.g., "128GB", "512GB", "1TB")
     */
    @GetMapping("/memories")
    public List<String> getAllMemoryOptions() {
        return productService.getAllMemoryOptions();
    }

    /**
     * Retrieves a list of products added in the last 7 days (new arrivals).
     * Projects results into a lightweight DTO to minimize payload size.
     *
     * @return a List of NewlyAddedProductDTO objects containing image, name, and base price
     */
    @GetMapping("/new")
    public List<NewlyAddedProductDTO> getNewlyCreatedProducts() {
        return productService.getNewlyCreatedProducts().stream()
                .map(p -> new NewlyAddedProductDTO(p.getImageUrl(), p.getName(), p.getBasePrice()))
                .toList();
    }

    /**
     * Retrieves a summary of products in a specific category.
     * Designed for category landing pages where full product details aren't needed.
     *
     * @param name the name of the category (e.g., "Laptops", "Smartphones")
     * @return a List of ProductByCategorySummaryDTO objects with minimal product data
     */
    @GetMapping("/category")
    public List<ProductByCategorySummaryDTO> getProductSummariesByCategory(@RequestParam String name) {
        return productService.getProductSummariesByCategory(name);
    }
}