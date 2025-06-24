package com.app.repositories;

import com.app.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Custom query methods can be defined here if needed
    // For example, to find categories by name:
    // List<Category> findByName(String name);
}
