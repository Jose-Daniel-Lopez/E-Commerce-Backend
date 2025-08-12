package com.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_variants")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 1000, message = "Product image Url cannot exceed 1000 characters")
    private String imageUrl;

    @Size(max = 50, message = "Size cannot exceed 50 characters")
    private String size;

    @Size(max = 30, message = "Color cannot exceed 30 characters")
    private String color;

    @Min(value = 0, message = "Stock must be at least 0")
    private Integer stock;

    @NotBlank(message = "SKU cannot be blank")
    private String sku; // Stock Keeping Unit

    // Many-to-One relationship with Product (bidirectional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Product product;

}
