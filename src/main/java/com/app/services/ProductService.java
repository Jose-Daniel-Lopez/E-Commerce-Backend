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

/**
 * Service class for handling business logic related to products.
 * Coordinates operations between the controller and repository layers.
 */
@Service
public class ProductService {

    private final ProductRepository productRepo;

    /**
     * Constructs a ProductService with the given ProductRepository.
     * Uses constructor injection for dependency management.
     *
     * @param productRepo the repository to interact with product data (injected via Spring)
     */
    @Autowired
    public ProductService(ProductRepository productRepo) {
        this.productRepo = productRepo;
    }

    /**
     * Retrieves all products from the database.
     * Note: Use with caution in production if the dataset is large; consider pagination.
     *
     * @return a List of all Product entities
     */
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    /**
     * Retrieves a single product by its unique ID.
     *
     * @param id the ID of the product to retrieve
     * @return the Product if found; otherwise, returns null
     */
    public Product getProductById(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    /**
     * Retrieves a paginated list of products belonging to a specific category.
     *
     * @param categoryId the ID of the category to filter by
     * @param pageable pagination configuration (page number, size, sorting)
     * @return a Page of Product entities in the specified category
     */
    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepo.findByCategoryId(categoryId, pageable);
    }

    /**
     * Retrieves a list of all distinct brand names available in the product catalog.
     * Useful for populating brand filter options in the frontend.
     *
     * @return a List of unique brand names as strings
     */
    public List<String> getAllBrands() {
        return productRepo.findAllDistinctBrands();
    }

    /**
     * A predefined list of standard memory options used across the application.
     * These values are static and not pulled from the database.
     * Can be extended or configured externally if needed.
     */
    private static final List<String> MEMORY_OPTIONS = List.of(
            "32GB", "64GB", "128GB", "256GB", "512GB", "1TB", "2TB", "4TB", "8TB"
    );

    /**
     * Returns a list of supported memory options for display in filters or forms.
     * These are predefined values and not dynamically fetched from products.
     *
     * @return a List of standard memory capacity strings (e.g., "128GB", "1TB")
     */
    public List<String> getAllMemoryOptions() {
        return MEMORY_OPTIONS;
    }

    /**
     * Retrieves products that were created in the last 7 days.
     * Used to highlight "new arrivals" or recently added items.
     *
     * @return a List of Product entities created within the past week
     */
    public List<Product> getNewlyCreatedProducts() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(7);
        return productRepo.findNewlyCreatedBetween(startDate, endDate);
    }

    /**
     * Retrieves a summary of products in a given category using a lightweight DTO.
     * Projects only essential fields (name, image URL, base price) to improve performance.
     *
     * @param categoryName the name of the category (e.g., "Laptops", "Smartphones")
     * @return a List of ProductByCategorySummaryDTO objects containing minimal product info
     */
    public List<ProductByCategorySummaryDTO> getProductSummariesByCategory(String categoryName) {
        return productRepo.findSummaryByCategoryName(categoryName);
    }
}