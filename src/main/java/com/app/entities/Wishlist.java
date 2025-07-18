package com.app.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@Entity
@Table(name = "wishlist")
public class Wishlist {

    // UNIQUE ENTITY FIELDS //
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Title cannot be null")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    private String title;

    @NotNull(message = "Description cannot be null")
    @Size(max = 1000, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Image URL cannot be null")
    @Size(max = 500, message = "Image URL cannot exceed 500 characters")
    private String imageUrl;

    @NotNull(message = "Product URL cannot be null")
    @Size(max = 500, message = "Product URL cannot exceed 500 characters")
    private String productUrl;

    @NotNull(message = "Price cannot be null")
    @Size(max = 20, message = "Price cannot exceed 20 characters")
    private String price;

    @NotNull(message = "Category cannot be null")
    @Size(max = 50, message = "Category cannot exceed 50 characters")
    private String category;


    // RELATIONSHIPS //
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(
            name = "wishlist_products",
            joinColumns = @JoinColumn(name = "wishlist_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private Set<Product> products;
}
