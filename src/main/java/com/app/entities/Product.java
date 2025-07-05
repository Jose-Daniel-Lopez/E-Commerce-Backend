package com.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    private String name;
    private String description;
    private String brand;

    @Column(name = "is_featured", nullable = false)
    @Builder.Default
    private Boolean isFeatured = false;

    private BigDecimal basePrice;
    private Integer totalStock;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductReview> productReviews = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<ProductVariant> productVariants = new ArrayList<>();

    // ========== SMARTPHONES ATTRIBUTES ==========
    @Column(name = "screen_size")
    private String screenSize;

    private String cpu;
    private String memory;

    @Column(name = "number_of_cores")
    private Integer numberOfCores;

    private String camera;

    @Column(name = "front_camera")
    private String frontCamera;

    private String battery;

    // ========== SMARTWATCHES ATTRIBUTES ==========
    @Column(name = "display_size")
    private String displaySize;

    @Column(name = "battery_life")
    private String batteryLife;

    @Column(name = "water_resistance")
    private String waterResistance;

    private String connectivity;

    @Column(name = "health_sensors")
    private String healthSensors;

    private String compatibility;

    // ========== CAMERAS ATTRIBUTES ==========
    private String resolution;

    @Column(name = "sensor_type")
    private String sensorType;

    @Column(name = "lens_mount")
    private String lensMount;

    @Column(name = "video_resolution")
    private String videoResolution;

    @Column(name = "iso_range")
    private String isoRange;

    @Column(name = "optical_zoom")
    private String opticalZoom;

    // ========== HEADPHONES ATTRIBUTES ==========
    @Column(name = "driver_size")
    private String driverSize;

    @Column(name = "frequency_response")
    private String frequencyResponse;

    private String impedance;

    @Column(name = "noise_cancel")
    private Boolean noiseCancel;

    @Column(name = "bluetooth_version")
    private String bluetoothVersion;

    // ========== COMPUTERS ATTRIBUTES ==========
    @Column(name = "processor_model")
    private String processorModel;

    @Column(name = "ram_capacity")
    private Integer ramCapacity;

    @Column(name = "storage_type")
    private String storageType;

    @Column(name = "storage_capacity")
    private Integer storageCapacity;

    @Column(name = "graphics_card")
    private String graphicsCard;

    @Column(name = "operating_system")
    private String operatingSystem;

    // ========== KEYBOARDS ATTRIBUTES ==========
    @Column(name = "key_type")
    private String keyType;

    private String layout;
    private String backlight;

    @Column(name = "key_profile")
    private String keyProfile;

    private Boolean ergonomic;

    // ========== MICE ATTRIBUTES ==========
    private Integer dpi;

    @Column(name = "programmable_buttons")
    private Boolean programmableButtons;

    // ========== GAMING ATTRIBUTES ==========
    private String platform;

    @Column(name = "game_genre")
    private String gameGenre;

    @Column(name = "player_count")
    private Integer playerCount;

    @Column(name = "online_multiplayer")
    private Boolean onlineMultiplayer;

    @Column(name = "system_requirements")
    private String systemRequirements;

    @Column(name = "age_rating")
    private String ageRating;

    // ========== TABLETS ATTRIBUTES ==========
    // Note: screenSize, resolution, storageCapacity, ramCapacity, operatingSystem, batteryLife are reused

    // ========== SMART HOME ATTRIBUTES ==========
    @Column(name = "power_source")
    private String powerSource;

    @Column(name = "control_method")
    private String controlMethod;

    @Column(name = "automation_features")
    private String automationFeatures;

    @Column(name = "security_features")
    private String securityFeatures;

    // ========== AUDIO ATTRIBUTES ==========
    @Column(name = "power_output")
    private Integer powerOutput;

    @Column(name = "speaker_configuration")
    private String speakerConfiguration;

    // ========== ACCESSORIES ATTRIBUTES ==========
    private String material;
    private String dimensions;
    private String weight;
    private String warranty;
    private String color;
}