package com.app.DTO;

import com.app.entities.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing a summarized view of a product,
 * intended for use in category-based listings where full product details are not required.
 *
 * <p>This DTO includes only essential fields to reduce payload size and improve performance
 * when displaying product overviews (e.g., on category landing pages).</p>
 *
 * <p>Used primarily in conjunction with custom JPQL queries in {@link com.app.repositories.ProductRepository}.</p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RelatedProductsDTO {


    /**
     * The unique identifier of the product.
     * Used for linking to detailed product views.
     */
    private Long id;

    /**
     * The name of the product (e.g., "UltraBook Pro X1").
     * This is typically displayed in product cards or listings.
     */
    private String name;

    /**
     * URL pointing to the main image of the product.
     * Should be a fully qualified or relative path accessible by the frontend.
     */
    private String imageUrl;


    /**
     * The category to which the product belongs (e.g., "Laptops", "Smartphones").
     * This helps in organizing products and enabling category-based filtering.
     */
    private Category category;

    /**
     * The base price of the product in the store's currency.
     * Represented as BigDecimal for precision in financial calculations.
     */
    private BigDecimal basePrice;
}