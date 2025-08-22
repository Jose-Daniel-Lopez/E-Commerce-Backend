package com.app.services;

import com.app.DTO.ProductByCategorySummaryDTO;
import com.app.DTO.RelatedProductsDTO;
import com.app.entities.Product;
import com.app.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing business logic related to {@link Product} entities.
 * <p>
 * This service acts as an intermediary between the controller and repository layers,
 * enforcing domain rules and optimizing data access. It supports operations such as:
 * </p>
 * <ul>
 *   <li>Retrieving products by category, brand, or recency</li>
 *   <li>Providing lightweight projections (DTOs) for performance</li>
 *   <li>Supplying static filter options (e.g., RAM, storage, OS)</li>
 *   <li>Supporting paginated and searchable product listings</li>
 * </ul>
 * <p>
 * Designed for extensibility — future enhancements may include caching, search integration,
 * inventory checks, or dynamic attribute handling.
 * </p>
 */
@Service
public class ProductService {

    // Repository for data access operations on Product entities
    private final ProductRepository productRepo;

    /**
     * Constructs a new ProductService with the required repository.
     *
     * @param productRepo the repository used to interact with product data; must not be null
     */
    @Autowired
    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    // === Product Retrieval Methods ===

    /**
     * Retrieves all products from the database.
     * <p>
     * ⚠️ <strong>Caution:</strong> This method loads all products into memory.
     * Avoid using it with large datasets in production. Prefer paginated queries.
     * </p>
     *
     * @return a list of all {@link Product} entities
     */
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    /**
     * Retrieves a product by its unique identifier.
     *
     * @param id the ID of the product to retrieve
     * @return the found {@link Product}, or null if not found
     */
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    // === Filtering & Search Methods ===

    /**
     * Retrieves products belonging to a specific category with pagination and sorting.
     *
     * @param categoryId the ID of the category to filter by
     * @param pageable   pagination and sorting configuration
     * @return a paged result of products in the specified category
     */
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepo.findByCategoryId(categoryId, pageable);
    }

    /**
     * Retrieves products created within the last 7 days ("new arrivals").
     *
     * @return a list of recently added {@link Product} entities
     */
    public List<Product> getNewlyCreatedProducts() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threeWeeksAgo = now.minusDays(21);
        return productRepo.findNewlyCreatedBetween(threeWeeksAgo, now);
    }

    /**
     * Retrieves a summary of products in a given category using a lightweight DTO.
     * Only essential fields (name, image URL, base price) are projected to reduce database load.
     *
     * @param categoryName the name of the target category (e.g., "Laptops", "Smartphones")
     * @return a list of {@link ProductByCategorySummaryDTO} objects
     */
    public List<ProductByCategorySummaryDTO> getProductSummariesByCategory(String categoryName) {
        return productRepo.findSummaryByCategoryName(categoryName);
    }

    // === SEARCH METHODS ===

    /**
     * Performs a comprehensive search across products based on a search term.
     * Searches through product names, brands, descriptions, and category names.
     *
     * @param searchTerm the term to search for (case-insensitive)
     * @param pageable   pagination and sorting configuration
     * @return a paged result of products matching the search term
     */
    public Page<Product> searchProducts(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return productRepo.findAll(pageable);
        }
        return productRepo.searchProducts(searchTerm.trim(), pageable);
    }

    /**
     * Searches products specifically by name (for more targeted searches).
     *
     * @param name     the product name to search for
     * @param pageable pagination and sorting configuration
     * @return a paged result of products with names matching the search term
     */
    public Page<Product> searchProductsByName(String name, Pageable pageable) {
        return productRepo.findByNameContainingIgnoreCase(name, pageable);
    }

    /**
     * Searches products by category name (for category-based searches).
     *
     * @param categoryName the category name to search for
     * @param pageable      pagination and sorting configuration
     * @return a paged result of products in categories matching the search term
     */
    public Page<Product> searchProductsByCategory(String categoryName, Pageable pageable) {
        return productRepo.findByCategoryNameContainingIgnoreCase(categoryName, pageable);
    }

    // === Utility & Filter Support Methods ===

    /**
     * Retrieves a list of all distinct brand names available in the catalog.
     * Useful for populating brand filters in the UI.
     *
     * @return a list of unique brand names as strings
     */
    public List<String> getAllBrands() {
        return productRepo.findAllDistinctBrands();
    }

    // === NEW FILTER OPTIONS (Modern Schema) ===

    /**
     * Returns a list of supported storage options for UI filters.
     *
     * @return immutable list of common storage sizes
     */
    public List<String> getAllStorageOptions() {
        return productRepo.findAllDistinctStorageOptions();
    }

    /**
     * Returns a list of supported RAM capacities (in GB).
     *
     * @return immutable list of RAM sizes
     */
    public List<Integer> getAllRamOptions() {
        return productRepo.findAllDistinctRamOptions();
    }

    /**
     * Returns a list of common operating systems for filtering.
     *
     * @return immutable list of OS names
     */
    public List<String> getOperatingSystems() {
        return productRepo.findAllDistinctOperatingSystems();
    }

    /**
     * Predefined list of keyboard switch types.
     */
    private static final List<String> SWITCH_TYPES = List.of(
            "Mechanical - Red Cherry MX",
            "Mechanical - Blue Kailh",
            "Optical",
            "Scissor",
            "Membrane",
            "Tactile",
            "Linear"
    );

    /**
     * Returns a list of supported switch types for mechanical keyboards.
     *
     * @return immutable list of switch type strings
     */
    public List<String> getSwitchTypes() {
        return SWITCH_TYPES;
    }

    /**
     * Predefined list of backlighting options.
     */
    private static final List<String> BACKLIGHTING_OPTIONS = List.of(
            "RGB", "Single-color", "White LED", "Per-key RGB", "None"
    );

    /**
     * Returns a list of backlighting features for input devices.
     *
     * @return immutable list of backlighting options
     */
    public List<String> getBacklightingOptions() {
        return BACKLIGHTING_OPTIONS;
    }

    /**
     * Fetches a list of related products from the same category, excluding the given product.
     * <p>
     * This method retrieves a random set of products from the database that share the same category
     * as the provided product ID. It's useful for displaying related items on product detail pages.
     * </p>
     *
     * @param productId the ID of the product for which to find related products
     * @param limit     the maximum number of related products to retrieve (max 10)
     * @return a list of {@link RelatedProductsDTO} containing related product information
     */
    public List<RelatedProductsDTO> getRelatedProducts(Long productId, int limit) {
        if (productId == null) return List.of();
        Product base = getProductById(productId);
        if (base == null || base.getCategory() == null) return List.of();
        int effectiveLimit = limit <= 0 ? 3 : Math.min(limit, 10); // capped to 10 max to prevent large random scans
        return productRepo.findRandomRelatedProducts(base.getCategory().getId(), productId, effectiveLimit)
                .stream()
                .map(p -> new RelatedProductsDTO(p.getId(), p.getName(), p.getImageUrl(), p.getCategory(), p.getBasePrice()))
                .toList();
    }
}