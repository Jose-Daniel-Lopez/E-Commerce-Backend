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
 * REST controller for handling product-related operations.
 * <p>
 * Provides endpoints for retrieving products by ID, category, featured status,
 * brand, and other filters. Supports pagination, HATEOAS enrichment, and
 * lightweight DTO projections for optimized client payloads.
 * </p>
 * <p>
 * Base URL: {@code /api/products}
 * </p>
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ProductRepository productRepo;
    private final HateoasLinkBuilder hateoasLinkBuilder;

    /**
     * Constructs a new ProductController with required dependencies.
     *
     * @param productService     the service handling product business logic
     * @param productRepo        the repository for direct database queries
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

    // === HATEOAS-ENHANCED ENDPOINTS ===

    /**
     * Retrieves a specific product by ID and enriches it with HATEOAS links.
     * <p>
     * The response includes navigational links to related resources such as
     * category, variants, reviews, and wishlists (if applicable).
     * </p>
     *
     * @param id the unique identifier of the product
     * @return {@link ResponseEntity} with {@link ProductRepresentation} if found; 404 if not found
     * @response 200 Successfully returns the HATEOAS-enabled product
     * @response 404 Product not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProductRepresentation> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        ProductRepresentation representation = hateoasLinkBuilder.buildProductRepresentation(product);
        return ResponseEntity.ok(representation);
    }

    // === CATEGORY-BASED ENDPOINTS ===

    /**
     * Retrieves a paginated list of products belonging to a specific category.
     * Supports sorting and pagination via standard query parameters (e.g., page, size, sort).
     *
     * @param categoryId the ID of the category to filter by
     * @param pageable   pagination and sorting instructions
     * @return a paginated list of {@link Product} entities
     * @response 200 Returns paginated products; empty page if no matches
     */
    @GetMapping("/category/{categoryId}")
    public Page<Product> getProductsByCategory(@PathVariable Long categoryId, Pageable pageable) {
        return productService.getProductsByCategory(categoryId, pageable);
    }

    /**
     * Retrieves a summary of products in a given category, optimized for category landing pages.
     * Uses a lightweight DTO to reduce payload size and improve performance.
     *
     * @param name the name of the category (e.g., "Smartphones", "Computers")
     * @return a list of {@link ProductByCategorySummaryDTO} objects
     * @response 200 Returns product summaries; empty list if no matches
     */
    @GetMapping("/category")
    public List<ProductByCategorySummaryDTO> getProductSummariesByCategory(@RequestParam String name) {
        return productService.getProductSummariesByCategory(name);
    }

    // === FEATURED & NEW ARRIVALS ===

    /**
     * Retrieves a paginated list of featured products.
     * Typically used for homepage highlights or promotional sections.
     *
     * @param pageable pagination and sorting parameters
     * @return a paginated list of products where {@code isFeatured = true}
     * @response 200 Returns featured products; empty page if none exist
     */
    @GetMapping("/featured")
    public Page<Product> getFeaturedProducts(Pageable pageable) {
        return productRepo.findByIsFeatured(true, pageable);
    }

    /**
     * Retrieves recently added products (last 7 days) as "new arrivals".
     * Projects data into a minimal DTO to optimize frontend loading.
     *
     * @return a list of {@link NewlyAddedProductDTO} objects
     * @response 200 Returns new products; empty list if none in the last 7 days
     */
    @GetMapping("/new")
    public List<NewlyAddedProductDTO> getNewlyCreatedProducts() {
        return productService.getNewlyCreatedProducts().stream()
                .map(p -> new NewlyAddedProductDTO(p.getImageUrl(), p.getName(), p.getBasePrice()))
                .toList();
    }

    // === FILTERING & OPTIONS ===

    /**
     * Retrieves all distinct brand names from the product catalog.
     * Used to populate brand filter dropdowns in the UI.
     *
     * @return a list of unique brand names (e.g., "Apple", "Samsung", "Sony")
     * @response 200 Returns list of brands; may be empty
     */
    @GetMapping("/brands")
    public List<String> getAllBrands() {
        return productService.getAllBrands();
    }

    /**
     * Retrieves a predefined list of memory options supported across products.
     * These are static values used for filtering (not dynamically pulled from DB).
     *
     * @return a list of common memory capacities (e.g., "128GB", "512GB", "1TB")
     * @response 200 Always returns the same list of standard memory sizes
     */
    @GetMapping("/memories")
    public List<String> getAllMemoryOptions() {
        return productService.getAllMemoryOptions();
    }
}