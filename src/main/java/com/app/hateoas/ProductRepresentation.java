package com.app.hateoas;

import com.app.entities.Product;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;

public class ProductRepresentation extends RepresentationModel<ProductRepresentation> {

    private Long id;
    private String name;
    private String description;
    private String brand;
    private Boolean isFeatured;
    private BigDecimal basePrice;
    private Integer totalStock;

    // Smartphone attributes
    private String screenSize;
    private String cpu;
    private String memory;
    private Integer numberOfCores;
    private String camera;
    private String frontCamera;
    private String battery;

    // Other category-specific attributes can be added here as needed

    public ProductRepresentation() {}

    public ProductRepresentation(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.description = product.getDescription();
        this.brand = product.getBrand();
        this.isFeatured = product.getIsFeatured();
        this.basePrice = product.getBasePrice();
        this.totalStock = product.getTotalStock();
        this.screenSize = product.getScreenSize();
        this.cpu = product.getCpu();
        this.memory = product.getMemory();
        this.numberOfCores = product.getNumberOfCores();
        this.camera = product.getCamera();
        this.frontCamera = product.getFrontCamera();
        this.battery = product.getBattery();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }

    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }

    public Integer getTotalStock() { return totalStock; }
    public void setTotalStock(Integer totalStock) { this.totalStock = totalStock; }

    public String getScreenSize() { return screenSize; }
    public void setScreenSize(String screenSize) { this.screenSize = screenSize; }

    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }

    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = memory; }

    public Integer getNumberOfCores() { return numberOfCores; }
    public void setNumberOfCores(Integer numberOfCores) { this.numberOfCores = numberOfCores; }

    public String getCamera() { return camera; }
    public void setCamera(String camera) { this.camera = camera; }

    public String getFrontCamera() { return frontCamera; }
    public void setFrontCamera(String frontCamera) { this.frontCamera = frontCamera; }

    public String getBattery() { return battery; }
    public void setBattery(String battery) { this.battery = battery; }
}
