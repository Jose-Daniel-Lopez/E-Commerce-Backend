package com.app.hateoas;

import com.app.entities.Product;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductRepresentation extends RepresentationModel<ProductRepresentation> {

    private Long id;
    private String name;
    private String description;
    private String brand;
    private Boolean isFeatured;
    private Double rating;
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
        this.rating = product.getRating();
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
}
