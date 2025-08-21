package com.app.repositories;

import com.app.DTO.ProductByCategorySummaryDTO;
import com.app.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for managing Product entities.
 * Provides CRUD operations and custom query methods via Spring Data JPA.
 *
 * <p>Now aligned with the updated Product schema supporting:</p>
 * <ul>
 *   <li><strong>Mobile & Compute</strong>: RAM, Storage, OS, GPU, CPU</li>
 *   <li><strong>Input & Control</strong>: DPI, Polling Rate, Switch Type</li>
 * </ul>
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Retrieves a paginated list of products filtered by featured status.
     *
     * @param isFeatured the featured status (true/false) to filter by
     * @param pageable pagination information (page number, size, sort)
     * @return a Page of Product entities matching the featured status
     */
    Page<Product> findByIsFeatured(Boolean isFeatured, Pageable pageable);

    /**
     * Retrieves a paginated list of products filtered by brand name.
     *
     * @param brand the brand name to filter products (case-sensitive)
     * @param pageable pagination information (page number, size, sort)
     * @return a Page of Product entities matching the brand
     */
    Page<Product> findByBrand(String brand, Pageable pageable);

    /**
     * Retrieves a list of all distinct brand names from products where brand is not null.
     * Useful for populating brand filters in the UI.
     *
     * @return a List of unique brand names as strings
     */
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL ORDER BY p.brand")
    List<String> findAllDistinctBrands();

    // ========================================================================
    // === MODERN FILTER QUERIES (REPLACE LEGACY ONES)
    // ========================================================================

    /**
     * Retrieves a list of all distinct storage values from products where storage is not null.
     * Used for filtering by capacity (e.g., "512GB", "1TB").
     *
     * @return a List of unique storage values as strings
     */
    @Query("SELECT DISTINCT p.storage FROM Product p WHERE p.storage IS NOT NULL")
    List<String> findAllDistinctStorageOptions();

    /**
     * Retrieves a list of all distinct RAM values (in GB) from products where ram is not null.
     * Used for filtering laptops, tablets, and smartphones.
     *
     * @return a List of unique RAM capacities as integers
     */
    @Query("SELECT DISTINCT p.ram FROM Product p WHERE p.ram IS NOT NULL ORDER BY p.ram")
    List<Integer> findAllDistinctRamOptions();

    /**
     * Retrieves a list of all distinct operating systems from products where os is not null.
     * Helps populate OS filters (e.g., Android, Windows, macOS).
     *
     * @return a List of unique OS names as strings
     */
    @Query("SELECT DISTINCT p.os FROM Product p WHERE p.os IS NOT NULL")
    List<String> findAllDistinctOperatingSystems();

    /**
     * Retrieves a list of all distinct switch types from products where switchType is not null.
     * Used for keyboard filtering (e.g., Mechanical, Optical).
     *
     * @return a List of unique switch types as strings
     */
    @Query("SELECT DISTINCT p.switchType FROM Product p WHERE p.switchType IS NOT NULL")
    List<String> findAllDistinctSwitchTypes();

    /**
     * Retrieves a list of all distinct backlighting options from products where backlighting is not null.
     *
     * @return a List of unique backlighting features (e.g., "RGB", "None")
     */
    @Query("SELECT DISTINCT p.backlighting FROM Product p WHERE p.backlighting IS NOT NULL")
    List<String> findAllDistinctBacklightingOptions();

    /**
     * Retrieves a list of all distinct DPI values from products where dpi is not null.
     * Useful for mouse filtering.
     *
     * @return a List of unique DPI values as integers
     */
    @Query("SELECT DISTINCT p.dpi FROM Product p WHERE p.dpi IS NOT NULL ORDER BY p.dpi DESC")
    List<Integer> findAllDistinctDpiOptions();

    /**
     * Retrieves a list of all distinct polling rates from products where pollingRate is not null.
     * Common in gaming peripherals.
     *
     * @return a List of unique polling rates (Hz)
     */
    @Query("SELECT DISTINCT p.pollingRate FROM Product p WHERE p.pollingRate IS NOT NULL")
    List<Integer> findAllDistinctPollingRates();

    // ========================================================================
    // === CATEGORY & PAGINATION
    // ========================================================================

    /**
     * Retrieves a paginated list of products belonging to a specific category.
     *
     * @param categoryId the ID of the category to filter by
     * @param pageable pagination information (page number, size, sort)
     * @return a Page of Product entities in the specified category
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    // ========================================================================
    // === UTILITY QUERIES
    // ========================================================================

    /**
     * Finds all products created within a specified date range.
     *
     * @param startDate the start of the date range (inclusive)
     * @param endDate the end of the date range (inclusive)
     * @return a List of Product entities created between startDate and endDate
     */
    @Query("SELECT p FROM Product p WHERE p.createdAt BETWEEN :startDate AND :endDate")
    List<Product> findNewlyCreatedBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    /**
     * Retrieves a summary of products in a given category using a DTO projection.
     * Selects only name, image URL, and base price to reduce data transfer.
     *
     * @param categoryName the name of the category (e.g., "Smartphones", "Laptops")
     * @return a List of ProductByCategorySummaryDTO objects containing limited product details
     */
    @Query("SELECT new com.app.DTO.ProductByCategorySummaryDTO(p.name, p.imageUrl, p.basePrice) " +
            "FROM Product p WHERE p.category.name = :categoryName")
    List<ProductByCategorySummaryDTO> findSummaryByCategoryName(
            @Param("categoryName") String categoryName);

    // ========================================================================
    // === SEARCH QUERIES
    // ========================================================================

    /**
     * Performs a global search across products by name, brand, description, and category name.
     * Uses case-insensitive LIKE queries to match partial text.
     *
     * @param searchTerm the search term to match against multiple fields
     * @param pageable pagination information (page number, size, sort)
     * @return a Page of Product entities matching the search criteria
     */
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.brand) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(p.category.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchProducts(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Searches products by name only (for more specific product searches).
     *
     * @param name the product name to search for
     * @param pageable pagination information
     * @return a Page of Product entities matching the name
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Product> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    /**
     * Searches products by category name (case-insensitive).
     *
     * @param categoryName the category name to search for
     * @param pageable pagination information
     * @return a Page of Product entities in categories matching the name
     */
    @Query("SELECT p FROM Product p WHERE LOWER(p.category.name) LIKE LOWER(CONCAT('%', :categoryName, '%'))")
    Page<Product> findByCategoryNameContainingIgnoreCase(@Param("categoryName") String categoryName, Pageable pageable);

    /**
     * Retrieves up to a specified number of random products from the same category
     * excluding the given product ID. Uses PostgreSQL RANDOM() for random ordering.
     *
     * @param categoryId the category to search within
     * @param productId the product to exclude from results
     * @param limit maximum number of related products to return
     * @return list of random related products (may contain fewer than limit if not enough products)
     */
    @Query(value = "SELECT * FROM products p WHERE p.category_id = :categoryId AND p.id <> :productId ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Product> findRandomRelatedProducts(@Param("categoryId") Long categoryId,
                                            @Param("productId") Long productId,
                                            @Param("limit") int limit);
}