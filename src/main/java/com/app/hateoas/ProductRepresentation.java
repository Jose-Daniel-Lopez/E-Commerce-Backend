package com.app.hateoas;

import com.app.entities.Product;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) representing a product in the REST API response.
 * Extends {@link RepresentationModel} to support HATEOAS (Hypermedia as the Engine of Application State).
 *
 * <p>This class is used to:</p>
 * <ul>
 *   <li>Decouple internal entity structure from API contract</li>
 *   <li>Add hypermedia links (e.g., self, related resources)</li>
 *   <li>Provide a stable API even if the underlying entity changes</li>
 * </ul>
 *
 * <p>It includes core attributes and supports both:</p>
 * <ul>
 *   <li><strong>Mobile & Compute</strong>: Phones, Tablets, Laptops</li>
 *   <li><strong>Input & Control</strong>: Mice, Keyboards, Controllers</li>
 * </ul>
 */
@Getter
@Setter
public class ProductRepresentation extends RepresentationModel<ProductRepresentation> {

    // =======================
    // 🔧 Core Product Attributes
    // =======================

    /**
     * Unique identifier of the product.
     * Used in URLs and link generation (e.g., /products/{id}).
     */
    private Long id;

    /**
     * Name of the product (e.g., "iPhone 15 Pro", "Galaxy S24").
     * Displayed prominently in UI and search results.
     */
    private String name;

    /**
     * Detailed description of the product.
     * May include features, benefits, or technical details.
     */
    private String description;

    /**
     * Manufacturer or brand name (e.g., "Apple", "Logitech").
     * Used for filtering and grouping in the frontend.
     */
    private String brand;

    /**
     * Indicates whether this product is featured on the homepage
     * or in promotional sections.
     */
    private Boolean isFeatured;

    /**
     * Average customer rating (0–5 scale).
     * May be null if no reviews exist.
     */
    private Double rating;

    /**
     * Base price of the product in the store's currency.
     * Stored as {@link BigDecimal} to avoid floating-point precision issues.
     */
    private BigDecimal basePrice;

    /**
     * Current stock level (number of units available).
     * Used to determine availability and display stock status.
     */
    private Integer totalStock;

    // ========================================================================
    // === MOBILE & COMPUTE (Phones, Tablets, Laptops, Handhelds)
    // ========================================================================

    /**
     * Screen size in inches (e.g., "6.7\"", "13.3\"").
     */
    private String screenSize;

    /**
     * CPU/processor name (e.g., "Snapdragon 8 Gen 3", "Apple M2").
     */
    private String cpu;

    /**
     * RAM in GB (e.g., 8, 16, 32).
     */
    private Integer ram;

    /**
     * Combined storage type and capacity (e.g., "512GB NVMe SSD", "256GB UFS").
     */
    private String storage;

    /**
     * Graphics processing unit (e.g., "NVIDIA RTX 4070", "Adreno 750").
     */
    private String gpu;

    /**
     * Refresh rate of the display in Hz (e.g., 60, 90, 120, 144).
     */
    private Integer refreshRate;

    /**
     * Rear camera specifications (e.g., "200MP", "48MP + 12MP Ultra-wide").
     */
    private String camera;

    /**
     * Front-facing camera specifications (e.g., "12MP").
     */
    private String frontCamera;

    /**
     * Battery capacity (e.g., "5000mAh").
     */
    private String battery;

    /**
     * Operating system (e.g., "Android 14", "Windows 11", "macOS Sonoma").
     */
    private String os;

    // ========================================================================
    // === INPUT & CONTROL (Mice, Keyboards, Controllers, Ergo Devices)
    // ========================================================================

    /**
     * DPI (dots per inch) sensitivity (e.g., 1600, 3200).
     */
    private Integer dpi;

    /**
     * Polling rate in Hz (e.g., 125, 500, 1000). Common in gaming peripherals.
     */
    private Integer pollingRate;

    /**
     * Key or switch type (e.g., "Mechanical - Red Cherry MX", "Optical").
     */
    private String switchType;

    /**
     * Backlighting features (e.g., "RGB", "White LED", "None").
     */
    private String backlighting;

    /**
     * Whether the device has programmable buttons.
     */
    private Boolean programmableButtons;

    /**
     * Battery life duration (e.g., "40 hours", "Up to 200 hours").
     */
    private String batteryLife;

    /**
     * Whether the device has an ergonomic design (e.g., split, vertical, ambidextrous).
     */
    private Boolean ergonomic;

    // =======================
    // 🛠️ Constructors
    // =======================

    /**
     * Default constructor required by Jackson for deserialization.
     * Initializes an empty instance.
     */
    public ProductRepresentation() {
        // Default constructor
    }

    /**
     * Constructs a {@link ProductRepresentation} from a {@link Product} entity.
     * Maps all relevant fields based on the product's category.
     *
     * @param product the source product entity; must not be {@code null}
     * @throws NullPointerException if {@code product} is {@code null}
     */
    public ProductRepresentation(Product product) {
        if (product == null) {
            throw new NullPointerException("Product cannot be null");
        }

        // Core attributes
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.brand = product.getBrand();
        this.isFeatured = product.getIsFeatured();
        this.rating = product.getRating();
        this.basePrice = product.getBasePrice();
        this.totalStock = product.getTotalStock();

        // Mobile & Compute fields
        this.screenSize = product.getScreenSize();
        this.cpu = product.getCpu();
        this.ram = product.getRam();
        this.storage = product.getStorage();
        this.gpu = product.getGpu();
        this.refreshRate = product.getRefreshRate();
        this.camera = product.getCamera();
        this.frontCamera = product.getFrontCamera();
        this.battery = product.getBattery();
        this.os = product.getOs();

        // Input & Control fields
        this.dpi = product.getDpi();
        this.pollingRate = product.getPollingRate();
        this.switchType = product.getSwitchType();
        this.backlighting = product.getBacklighting();
        this.programmableButtons = product.getProgrammableButtons();
        this.batteryLife = product.getBatteryLife();
        this.ergonomic = product.getErgonomic();
    }
}