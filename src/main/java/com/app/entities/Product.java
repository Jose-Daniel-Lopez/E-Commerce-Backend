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
    @Size(max = 255, message = "Image URL cannot exceed 255 characters")
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

    // ========== SMARTPHONES ATTRIBUTES ==========

    /**
     * Screen size in inches (e.g., "6.8").
     */
    @Column(name = "screen_size")
    private String screenSize;

    /**
     * CPU/processor name (e.g., "Snapdragon 8 Gen 3").
     */
    private String cpu;

    /**
     * Internal memory/storage capacity (e.g., "128GB", "256GB").
     */
    private String memory;

    /**
     * Number of CPU cores (e.g., 8).
     */
    @Column(name = "number_of_cores")
    private Integer numberOfCores;

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

    // ========== SMARTWATCHES ATTRIBUTES ==========

    /**
     * Display size in inches (e.g., "1.4").
     */
    @Column(name = "display_size")
    private String displaySize;

    /**
     * Battery life duration (e.g., "7 days", "48 hours").
     */
    @Column(name = "battery_life")
    private String batteryLife;

    /**
     * Water resistance rating (e.g., "5ATM", "IP68").
     */
    @Column(name = "water_resistance")
    private String waterResistance;

    /**
     * Connectivity options (e.g., "Bluetooth 5.3, Wi-Fi, GPS").
     */
    private String connectivity;

    /**
     * Health monitoring sensors (e.g., "Heart rate, SpO2, ECG").
     */
    @Column(name = "health_sensors")
    private String healthSensors;

    /**
     * Device compatibility (e.g., "iOS & Android", "Windows only").
     */
    private String compatibility;

    // ========== CAMERAS ATTRIBUTES ==========

    /**
     * Image resolution (e.g., "24.2 MP").
     */
    private String resolution;

    /**
     * Sensor type (e.g., "CMOS", "APS-C", "Full Frame").
     */
    @Column(name = "sensor_type")
    private String sensorType;

    /**
     * Lens mount standard (e.g., "EF", "E", "Z").
     */
    @Column(name = "lens_mount")
    private String lensMount;

    /**
     * Maximum video resolution supported (e.g., "4K UHD", "1080p").
     */
    @Column(name = "video_resolution")
    private String videoResolution;

    /**
     * ISO sensitivity range (e.g., "100-6400", "80-102400").
     */
    @Column(name = "iso_range")
    private String isoRange;

    /**
     * Optical zoom capability (e.g., "3x", "10x").
     */
    @Column(name = "optical_zoom")
    private String opticalZoom;

    // ========== HEADPHONES ATTRIBUTES ==========

    /**
     * Driver unit size in millimeters (e.g., "40mm", "50mm").
     */
    @Column(name = "driver_size")
    private String driverSize;

    /**
     * Frequency response range (e.g., "20Hz - 20kHz").
     */
    @Column(name = "frequency_response")
    private String frequencyResponse;

    /**
     * Electrical impedance in ohms (e.g., "32Ω").
     */
    private String impedance;

    /**
     * Whether the headphones support active noise cancellation.
     */
    @Column(name = "noise_cancel")
    private Boolean noiseCancel;

    /**
     * Bluetooth version (e.g., "5.0", "5.3").
     */
    @Column(name = "bluetooth_version")
    private String bluetoothVersion;

    // ========== COMPUTERS ATTRIBUTES ==========

    /**
     * Processor model (e.g., "Intel Core i7-13700K", "Apple M2").
     */
    @Column(name = "processor_model")
    private String processorModel;

    /**
     * RAM capacity in GB (e.g., 16, 32).
     */
    @Column(name = "ram_capacity")
    private Integer ramCapacity;

    /**
     * Type of storage (e.g., "SSD", "HDD", "NVMe").
     */
    @Column(name = "storage_type")
    private String storageType;

    /**
     * Storage capacity in GB or TB (e.g., 512, 1024).
     */
    @Column(name = "storage_capacity")
    private Integer storageCapacity;

    /**
     * Graphics card model (e.g., "NVIDIA RTX 4070", "AMD Radeon 780M").
     */
    @Column(name = "graphics_card")
    private String graphicsCard;

    /**
     * Pre-installed operating system (e.g., "Windows 11", "macOS Sonoma").
     */
    @Column(name = "operating_system")
    private String operatingSystem;

    // ========== KEYBOARDS ATTRIBUTES ==========

    /**
     * Key switch type (e.g., "Mechanical", "Membrane", "Optical").
     */
    @Column(name = "key_type")
    private String keyType;

    /**
     * Keyboard layout (e.g., "QWERTY", "AZERTY", "60%").
     */
    private String layout;

    /**
     * Backlighting features (e.g., "RGB", "White", "None").
     */
    private String backlight;

    /**
     * Keycap profile (e.g., "OEM", "Cherry", "DSA").
     */
    @Column(name = "key_profile")
    private String keyProfile;

    /**
     * Whether the keyboard has an ergonomic design (e.g., split, curved).
     */
    private Boolean ergonomic;

    // ========== MICE ATTRIBUTES ==========

    /**
     * DPI (dots per inch) sensitivity (e.g., 1600, 3200).
     */
    private Integer dpi;

    /**
     * Whether the mouse has programmable buttons.
     */
    @Column(name = "programmable_buttons")
    private Boolean programmableButtons;

    // ========== GAMING ATTRIBUTES ==========

    /**
     * Target platform (e.g., "PC", "PS5", "Xbox Series X", "Nintendo Switch").
     */
    private String platform;

    /**
     * Genre of the game (e.g., "Action", "RPG", "Strategy").
     */
    @Column(name = "game_genre")
    private String gameGenre;

    /**
     * Maximum number of players (e.g., 1, 4, Online Only).
     */
    @Column(name = "player_count")
    private Integer playerCount;

    /**
     * Whether the game supports online multiplayer.
     */
    @Column(name = "online_multiplayer")
    private Boolean onlineMultiplayer;

    /**
     * Minimum and recommended system requirements.
     * May include CPU, RAM, GPU, and storage.
     */
    @Column(name = "system_requirements")
    private String systemRequirements;

    /**
     * Age rating (e.g., "PEGI 16", "E10+", "Mature").
     */
    @Column(name = "age_rating")
    private String ageRating;

    // ========== TABLETS ATTRIBUTES ==========
    // Note: Reuses common fields like screenSize, resolution, storageCapacity,
    //       ramCapacity, operatingSystem, and batteryLife from other categories.

    // ========== SMART HOME ATTRIBUTES ==========

    /**
     * Power source (e.g., "Battery", "USB", "Hardwired").
     */
    @Column(name = "power_source")
    private String powerSource;

    /**
     * Control method (e.g., "App", "Voice Assistant", "Remote").
     */
    @Column(name = "control_method")
    private String controlMethod;

    /**
     * Automation capabilities (e.g., "Scheduling", "Scenes", "IFTTT").
     */
    @Column(name = "automation_features")
    private String automationFeatures;

    /**
     * Security features (e.g., "End-to-end encryption", "Two-factor auth").
     */
    @Column(name = "security_features")
    private String securityFeatures;

    // ========== AUDIO ATTRIBUTES ==========

    /**
     * Total power output in watts (e.g., 50 for speakers).
     */
    @Column(name = "power_output")
    private Integer powerOutput;

    /**
     * Speaker configuration (e.g., "2.1", "5.1", "Stereo").
     */
    @Column(name = "speaker_configuration")
    private String speakerConfiguration;

    // ========== ACCESSORIES ATTRIBUTES ==========

    /**
     * Material of the product (e.g., "Plastic", "Aluminum", "Leather").
     */
    private String material;

    /**
     * Physical dimensions (e.g., "15 x 8 x 1 cm").
     */
    private String dimensions;

    /**
     * Weight in grams or kilograms (e.g., "250g").
     */
    private String weight;

    /**
     * Warranty period (e.g., "1 year", "Lifetime").
     */
    private String warranty;

    /**
     * Color or finish of the product (e.g., "Black", "Silver", "Rose Gold").
     */
    private String color;
}