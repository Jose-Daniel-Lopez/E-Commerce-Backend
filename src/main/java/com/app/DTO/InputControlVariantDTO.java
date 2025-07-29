package com.app.DTO;

import com.app.entities.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Product Variants in the INPUT & CONTROL category.
 * This includes: Mice, Keyboards, and Controllers.
 *
 * <p>This DTO does not expose storage information since input devices
 * typically don't have storage configurations. Instead, they may have
 * switch types, DPI settings, or edition types stored in the size field.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InputControlVariantDTO {

    /**
     * Unique identifier of the product variant.
     */
    private Long id;

    /**
     * Color of the product variant.
     * Limited to: Red, White, Black, Blue as per seeder configuration.
     */
    private String color;

    /**
     * Stock Keeping Unit - unique identifier for inventory tracking.
     */
    private String sku;

    /**
     * Available stock quantity for this specific variant.
     */
    private Integer stock;

    /**
     * Creates an InputControlVariantDTO from a ProductVariant entity.
     * Note: The 'size' field from ProductVariant is intentionally excluded
     * as it contains switch types, DPI variants, or edition types which
     * are not relevant for the simplified INPUT & CONTROL variant view.
     *
     * @param variant the ProductVariant entity to convert
     * @return a new InputControlVariantDTO instance
     */
    public static InputControlVariantDTO fromProductVariant(ProductVariant variant) {
        return InputControlVariantDTO.builder()
                .id(variant.getId())
                .color(variant.getColor())
                .sku(variant.getSku())
                .stock(variant.getStock())
                .build();
    }
}
