package com.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Entity representing a product in the e-commerce system.
 * Supports multiple product types (smartphones, laptops, cameras, etc.) through a flexible
 * attribute-based design where optional fields are used based on category.
 *
 * <p>This class uses inheritance via composition rather than database inheritance
 * to simplify querying and avoid complex joins. Category-specific attributes are stored
 * directly in this table and may be null depending on the product type.</p>
 *
 * <p>Key features:</p>
 * <ul>
 *   <li>Supports HATEOAS and DTO projections via selective field exposure.</li>
 *   <li>Includes validation constraints for data integrity.</li>
 *   <li>Manages relationships with reviews, variants, categories, and wishlists.</li>
 * </ul>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "products")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The name of the product (e.g., "Galaxy S24 Ultra").
     * Must not be blank. Used in displays, search, and SEO.
     */
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    /**
     * Detailed description of the product.
     * Should include key features, usage, and benefits. Cannot be blank.
     */
    @NotBlank(message = "Product description cannot be blank")
    private String description;

    /**
     * Manufacturer or brand of the product (e.g., "Apple", "Samsung").
     * Used for filtering and branding. Cannot be blank.
     */
    @NotBlank(message = "Product brand cannot be blank")
    private String brand;

    /**
     * Timestamp when the product was created.
     * Automatically set on creation; not modifiable.
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Value indicating the product rating.
     * Must be between 0.0 and 5.0, inclusive.
     * Used for sorting and filtering products by quality.
     * Defaults to 0.0 if not explicitly set.
     */
    @Column(name = "rating", nullable = false)
    @Builder.Default
    @DecimalMin(value = "0.0", inclusive = true, message = "Rating must be at least 0.0")
    @DecimalMax(value = "5.0", inclusive = true, message = "Rating must be at most 5.0")
    private Double rating = 0.0;

    /**
     * Flag indicating whether the product is featured on the homepage or promotions.
     * Defaults to {@code false} if not explicitly set.
     */
    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    /**
     * URL to the main image of the product.
     * Limited to 255 characters. Should point to CDN or static asset path.
     */
    @Size(max = 1000, message = "Image URL cannot exceed 1000 characters")
    @Column(name = "image_url")
    private String imageUrl;

    /**
     * Base price of the product in the store's currency.
     * Must be non-negative. Stored as BigDecimal for precision.
     */
    @DecimalMin(value = "0.0", inclusive = true, message = "Base price must be at least 0")
    private BigDecimal basePrice;

    /**
     * Total available stock for this product.
     * Includes inventory across all variants. Must be non-negative.
     */
    @Min(value = 0, message = "Total stock must be at least 0")
    private Integer totalStock;

    // ========== RELATIONSHIPS ==========
    /**
     * List of reviews associated with this product.
     * Managed by the {@link ProductReview} entity. Cascaded on delete.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductReview> productReviews = new ArrayList<>();

    /**
     * Category to which this product belongs (e.g., "Smartphones", "Laptops").
     * Eagerly fetched to avoid lazy loading issues in JSON serialization.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    /**
     * List of variants for this product (e.g., different colors, storage sizes).
     * Each variant may have its own price and stock. Cascaded on save/delete.
     */
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductVariant> productVariants = new ArrayList<>();

    /**
     * Set of wishlists that include this product.
     * Bidirectional ManyToMany; ignored during JSON serialization to prevent cycles.
     */
    @ManyToMany(mappedBy = "products")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Wishlist> wishlists = new HashSet<>();

    // ========================================================================
    // === MOBILE & COMPUTE (Phones, Tablets, Laptops, Handhelds)
    // ========================================================================

    /**
     * Screen size in inches (e.g., "6.8").
     */
    @Column(name = "screen_size")
    private String screenSize;

    /**
     * CPU/processor name (e.g., "Snapdragon 8 Gen 3", "Apple M2").
     */
    private String cpu;

    /**
     * RAM in GB (e.g., 8, 16, 32). Replaces old ramCapacity.
     */
    @Column(name = "ram")
    private Integer ram;

    /**
     * Combined storage type and capacity (e.g., "512GB NVMe SSD", "256GB UFS 4.0").
     * Replaces storageType and storageCapacity.
     */
    @Column(name = "storage")
    private String storage;

    /**
     * Graphics processing unit (e.g., "NVIDIA RTX 4070", "AMD Radeon 780M").
     * Replaces 'graphicsCard'.
     */
    @Column(name = "gpu")
    private String gpu;

    /**
     * Refresh rate of the display in Hz (e.g., 60, 90, 120, 144).
     */
    @Column(name = "refresh_rate")
    private Integer refreshRate;

    /**
     * Rear camera specifications (e.g., "200MP, f/1.7").
     */
    private String camera;

    /**
     * Front-facing camera specifications (e.g., "12MP").
     */
    @Column(name = "front_camera")
    private String frontCamera;

    /**
     * Battery capacity or life (e.g., "5000mAh", "Up to 18h").
     */
    private String battery;

    /**
     * Operating system (e.g., "Android 14", "Windows 11", "iOS 17").
     * Replaces 'operatingSystem'.
     */
    @Column(name = "os")
    private String os;

    // ========================================================================
    // === INPUT & CONTROL (Mice, Keyboards, Controllers)
    // ========================================================================

    /**
     * DPI (dots per inch) sensitivity (e.g., 1600, 3200).
     */
    private Integer dpi;

    /**
     * Polling rate in Hz (e.g., 125, 500, 1000). Common in gaming mice/keyboards.
     */
    @Column(name = "polling_rate")
    private Integer pollingRate;

    /**
     * Key or switch type (e.g., "Mechanical - Red Cherry MX", "Optical", "Scissor").
     * Replaces 'keyType'.
     */
    @Column(name = "switch_type")
    private String switchType;

    /**
     * Backlighting features (e.g., "RGB", "White", "None").
     * Replaces 'backlight'.
     */
    @Column(name = "backlighting")
    private String backlighting;

    /**
     * Whether the device has programmable buttons.
     */
    @Column(name = "programmable_buttons")
    private Boolean programmableButtons;

    /**
     * Battery life duration (e.g., "7 days", "48 hours"). Reused from smartwatches.
     */
    @Column(name = "battery_life")
    private String batteryLife;
}