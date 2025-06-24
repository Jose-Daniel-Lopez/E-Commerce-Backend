package com.app.repositories;

import com.app.entities.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    // Custom query methods can be added here if needed
    // For example, to find by size or color:
    // List<ProductVariant> findBySize(String size);
    // List<ProductVariant> findByColor(String color);
}
