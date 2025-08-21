package com.app.controllers;

import com.app.DTO.NewlyAddedProductDTO;
import com.app.DTO.ProductByCategorySummaryDTO;
import com.app.DTO.RelatedProductsDTO;
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

    /**
     * Retrieves a list of up to 3 related products based on the given product ID.
     * <p>
     * Related products are determined by the backend service, potentially using
     * algorithms that consider product attributes, categories, and other factors.
     * </p>
     *
     * @param id the unique identifier of the product
     * @param limit optional limit for the number of related products to return (default is 3)
     * @return {@link ResponseEntity} with a list of {@link RelatedProductsDTO} objects
     * @response 200 Successfully returns the related products; may be empty if none found
     * @response 404 Product not found
     */
    @GetMapping("/{id}/related")
    public ResponseEntity<List<RelatedProductsDTO>> getRelatedProducts(
            @PathVariable Long id,
            @RequestParam(value = "limit", required = false, defaultValue = "3") int limit) {
        Product base = productService.getProductById(id);
        if (base == null) {
            return ResponseEntity.notFound().build();
        }
        List<RelatedProductsDTO> related = productService.getRelatedProducts(id, limit);
        return ResponseEntity.ok(related);
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
                .map(p -> new NewlyAddedProductDTO(p.getId(), p.getImageUrl(), p.getName(), p.getBasePrice()))
                .toList();
    }

    // === FILTERING & OPTIONS ===

    /**
     * Retrieves all distinct brand names from the product catalog.
     * Used to populate brand filter dropdowns in the UI.
     *
     * @return a list of unique brand names (e.g., "Apple", "Samsung", "Logitech")
     * @response 200 Returns list of brands; may be empty
     */
    @GetMapping("/brands")
    public List<String> getAllBrands() {
        return productService.getAllBrands();
    }

    /**
     * Retrieves a predefined list of storage options supported across products.
     * These are static values used for filtering (not dynamically pulled from DB).
     * Replaces the old 'memory' concept with modern 'storage' terminology.
     *
     * @return a list of common storage capacities (e.g., "128GB", "512GB", "1TB")
     * @response 200 Always returns the same list of standard storage sizes
     */
    @GetMapping("/storage")
    public List<String> getAllStorageOptions() {
        return productService.getAllStorageOptions();
    }

    /**
     * Retrieves a predefined list of RAM options in GB.
     * Used for filtering laptops, tablets, and smartphones.
     *
     * @return a list of common RAM sizes (e.g., 8, 16, 32)
     * @response 200 Always returns the same list of standard RAM sizes
     */
    @GetMapping("/ram")
    public List<Integer> getAllRamOptions() {
        return productService.getAllRamOptions();
    }

    /**
     * Retrieves a list of operating systems commonly used in the catalog.
     * Helps populate OS filters for Mobile & Compute devices.
     *
     * @return a list of common OS names (e.g., "Android", "Windows 11", "macOS")
     * @response 200 Returns list of operating systems
     */
    @GetMapping("/os")
    public List<String> getOperatingSystems() {
        return productService.getOperatingSystems();
    }

    /**
     * Retrieves a list of switch types for keyboards.
     * Used in filtering mechanical and membrane keyboards.
     *
     * @return a list of switch types (e.g., "Mechanical - Red Cherry MX", "Optical")
     * @response 200 Returns list of switch types
     */
    @GetMapping("/switch-types")
    public List<String> getSwitchTypes() {
        return productService.getSwitchTypes();
    }

    /**
     * Retrieves a list of backlighting options for input devices.
     *
     * @return a list of backlighting features (e.g., "RGB", "White LED", "None")
     * @response 200 Returns list of backlighting options
     */
    @GetMapping("/backlighting")
    public List<String> getBacklightingOptions() {
        return productService.getBacklightingOptions();
    }

    // === SEARCH ENDPOINTS ===

    /**
     * Main search endpoint for the frontend search bar.
     * Performs a comprehensive search across product names, brands, descriptions, and categories.
     * Can handle both specific product searches (e.g., "iPhone 13") and category searches (e.g., "smartphones").
     *
     * @param searchQuery the search query string from the frontend search bar
     * @param pageable pagination and sorting parameters (page, size, sort)
     * @return a paginated list of products matching the search criteria
     * @response 200 Returns matching products; empty page if no matches found
     *
     * @example GET /api/products/search?q=iPhone%2013&page=0&size=10&sort=name,asc
     * @example GET /api/products/search?q=smartphones&page=0&size=20
     */
    @GetMapping("/search")
    public Page<Product> searchProducts(
            @RequestParam("q") String searchQuery,
            Pageable pageable) {
        return productService.searchProducts(searchQuery, pageable);
    }

    /**
     * Advanced search endpoint that allows searching by specific criteria.
     * Useful for more targeted searches when the frontend needs to search by specific fields.
     *
     * @param name optional product name to search for
     * @param category optional category name to search for
     * @param brand optional brand name to search for
     * @param pageable pagination and sorting parameters
     * @return a paginated list of products matching the specific criteria
     * @response 200 Returns matching products based on specified criteria
     */
    @GetMapping("/search/advanced")
    public Page<Product> advancedSearch(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "brand", required = false) String brand,
            Pageable pageable) {

        // If only name is provided, search by name
        if (name != null && !name.trim().isEmpty() && category == null && brand == null) {
            return productService.searchProductsByName(name, pageable);
        }

        // If only category is provided, search by category
        if (category != null && !category.trim().isEmpty() && name == null && brand == null) {
            return productService.searchProductsByCategory(category, pageable);
        }

        // If brand is provided, search by brand
        if (brand != null && !brand.trim().isEmpty() && name == null && category == null) {
            return productRepo.findByBrand(brand, pageable);
        }

        // If multiple criteria or no specific criteria, fall back to general search
        String searchTerm = "";
        if (name != null) searchTerm += name + " ";
        if (category != null) searchTerm += category + " ";
        if (brand != null) searchTerm += brand + " ";

        return productService.searchProducts(searchTerm.trim(), pageable);
    }
}