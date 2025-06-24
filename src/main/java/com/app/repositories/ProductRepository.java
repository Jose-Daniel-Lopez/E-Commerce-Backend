package com.app.repositories;

import com.app.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Custom query methods can be defined here if needed
    // For example, to find products by name:
    // List<Product> findByNameContainingIgnoreCase(String name);
}
