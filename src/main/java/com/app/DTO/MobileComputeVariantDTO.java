package com.app.DTO;

import com.app.entities.ProductVariant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Product Variants in the MOBILE & COMPUTE category.
 * This includes: Smartphones, Tablets, Laptops, and Handhelds.
 *
 * <p>This DTO exposes storage information which is relevant for devices
 * that have different storage configurations (e.g., 128GB, 256GB, 512GB).</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MobileComputeVariantDTO {

    /**
     * Unique identifier of the product variant.
     */
    private Long id;

    /**
     * Storage configuration for the variant.
     * For mobile/compute devices, this represents storage capacity
     * (e.g., "128GB", "256GB", "512GB", "1TB") or RAM/Storage combinations
     * for laptops (e.g., "8GB/256GB", "16GB/512GB").
     */
    private String storage;

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
     * Creates a MobileComputeVariantDTO from a ProductVariant entity.
     * Maps the 'size' field from ProductVariant to 'storage' in this DTO,
     * as the size field is used to store storage information for mobile/compute products.
     *
     * @param variant the ProductVariant entity to convert
     * @return a new MobileComputeVariantDTO instance
     */
    public static MobileComputeVariantDTO fromProductVariant(ProductVariant variant) {
        return MobileComputeVariantDTO.builder()
                .id(variant.getId())
                .storage(variant.getSize()) // Size field contains storage info for mobile/compute
                .color(variant.getColor())
                .sku(variant.getSku())
                .stock(variant.getStock())
                .build();
    }
}
