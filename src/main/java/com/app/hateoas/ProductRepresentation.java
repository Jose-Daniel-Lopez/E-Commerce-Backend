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
 * <p>This class is used to:
 * <ul>
 *   <li>Decouple internal entity structure from API contract</li>
 *   <li>Add hypermedia links (e.g., self, related resources)</li>
 *   <li>Provide a stable API even if the underlying entity changes</li>
 * </ul>
 *
 * <p>It includes common product attributes and smartphone-specific fields.
 * Future category-specific fields (e.g., screen type, protection class) can be added as needed.
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
     * Name of the product (e.g., "Smartphone X200").
     * Displayed prominently in UI and search results.
     */
    private String name;

    /**
     * Detailed description of the product.
     * May include features, benefits, or technical details.
     */
    private String description;

    /**
     * Manufacturer or brand name (e.g., "TechCorp", "PixelTech").
     * Used for filtering and grouping in the frontend.
     */
    private String brand;

    /**
     * Indicates whether this product is featured on the homepage
     * or in promotional sections.
     */
    private Boolean isFeatured;

    /**
     * Average customer rating (typically 0–5).
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

    // =======================
    // 📱 Smartphone-Specific Attributes
    // =======================

    /**
     * Screen size in inches (e.g., "6.5 inches").
     * Used in filtering and product comparison.
     */
    private String screenSize;

    /**
     * CPU/processor model (e.g., "Snapdragon 8 Gen 2").
     * Part of the technical specification.
     */
    private String cpu;

    /**
     * Internal memory/RAM capacity (e.g., "8GB").
     * Used in filtering and product selection.
     */
    private String memory;

    /**
     * Number of CPU cores (e.g., 8).
     * Indicates processing power.
     */
    private Integer numberOfCores;

    /**
     * Rear camera specifications (e.g., "48MP + 12MP Dual Camera").
     * Displayed in product details and specs table.
     */
    private String camera;

    /**
     * Front-facing (selfie) camera specifications (e.g., "12MP").
     * Relevant for users interested in video calls or selfies.
     */
    private String frontCamera;

    /**
     * Battery capacity and type (e.g., "5000mAh Li-Ion").
     * Important for user experience and filtering.
     */
    private String battery;

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
     * Maps all relevant fields from the domain entity to the API representation.
     *
     * @param product the source product entity; must not be {@code null}
     * @throws NullPointerException if {@code product} is {@code null}
     */
    public ProductRepresentation(Product product) {
        if (product == null) {
            throw new NullPointerException("Product cannot be null");
        }

        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.brand = product.getBrand();
        this.isFeatured = product.getIsFeatured();
        this.rating = product.getRating();
        this.basePrice = product.getBasePrice();
        this.totalStock = product.getTotalStock();

        // Smartphone-specific attributes
        this.screenSize = product.getScreenSize();
        this.cpu = product.getCpu();
        this.memory = product.getMemory();
        this.numberOfCores = product.getNumberOfCores();
        this.camera = product.getCamera();
        this.frontCamera = product.getFrontCamera();
        this.battery = product.getBattery();
    }

    // Note: Lombok generates equals(), hashCode(), and toString() implicitly.
    // If custom behavior is needed, override them explicitly.
}