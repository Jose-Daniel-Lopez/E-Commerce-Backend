package com.app.services;

import com.app.DTO.ProductByCategorySummaryDTO;
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
 *   <li>Supplying static filter options (e.g., memory sizes)</li>
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
     * @return the found {@link Product}, or empty {@link Optional} if not found
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
        LocalDateTime oneWeekAgo = now.minusDays(7);
        return productRepo.findNewlyCreatedBetween(oneWeekAgo, now);
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

    /**
     * Predefined list of standard memory capacities used across the application.
     * These values are static and not sourced from the database.
     * Can be externalized to config files if needed in the future.
     */
    private static final List<String> MEMORY_OPTIONS = List.of(
            "32GB", "64GB", "128GB", "256GB", "512GB", "1TB", "2TB", "4TB", "8TB"
    );

    /**
     * Returns a list of supported memory options for use in filters or forms.
     * These are static values and not dynamically derived from product data.
     *
     * @return an immutable list of standard memory capacity strings (e.g., "128GB", "1TB")
     */
    public List<String> getAllMemoryOptions() {
        return MEMORY_OPTIONS;
    }

    // === Future Method Suggestions ===
    //
    // public Product createProduct(Product product) { ... }
    // public Optional<Product> updateProduct(Long id, Product updatedProduct) { ... }
    // public void deleteProduct(Long id) { ... }
    // public Page<Product> searchByName(String keyword, Pageable pageable) { ... }
    // public List<Product> getTopSelling(int limit) { ... }
}