package com.app.repositories;

import com.app.entities.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByIsFeatured(Boolean isFeatured, Pageable pageable);
    Page<Product> findByBrand(String brand, Pageable pageable);

    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.brand IS NOT NULL")
    List<String> findAllDistinctBrands();

    @Query("SELECT DISTINCT p.memory FROM Product p WHERE p.memory IS NOT NULL")
    List<String> findAllDistinctMemoryOptions();

}
