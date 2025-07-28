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
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL")
    List<String> findAllDistinctBrands();

    /**
     * Retrieves a list of all distinct memory options from products where memory is not null.
     * Useful for populating memory filters (e.g., 64GB, 128GB) in the UI.
     *
     * @return a List of unique memory values as strings
     */
    @Query("SELECT DISTINCT p.memory FROM Product p WHERE p.memory IS NOT NULL")
    List<String> findAllDistinctMemoryOptions();

    /**
     * Retrieves a paginated list of products belonging to a specific category.
     *
     * @param categoryId the ID of the category to filter by
     * @param pageable pagination information (page number, size, sort)
     * @return a Page of Product entities in the specified category
     */
    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

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
}