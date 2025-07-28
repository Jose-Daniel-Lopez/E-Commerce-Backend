package com.app.services;

import com.app.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing business logic related to {@code Category} entities.
 * <p>
 * This service acts as an intermediary between the web layer (controllers)
 * and the data access layer ({@link CategoryRepository}), ensuring that domain rules
 * and cross-cutting concerns (e.g., validation, caching, auditing) can be applied
 * consistently when working with product categories.
 * </p>
 * <p>
 * Currently, this class is minimal and only holds a reference to the repository.
 * It is designed to support future operations such as:
 * </p>
 * <ul>
 *   <li>Retrieving all categories (possibly in a tree hierarchy)</li>
 *   <li>Validating category names or slugs before creation</li>
 *   <li>Handling soft-deletes or archive states</li>
 *   <li>Enforcing constraints on nested categories (e.g., depth limits)</li>
 * </ul>
 * </p>
 */
@Service
public class CategoryService {

    // Repository for performing data persistence operations on Category entities
    private final CategoryRepository categoryRepo;

    /**
     * Constructs a new CategoryService with the required repository.
     *
     * @param categoryRepo the repository used to interact with category data; must not be null
     */
    @Autowired
    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    // Future methods (to be implemented as needed):
    //
    // public List<Category> getAllCategories() { ... }
    // public Category createCategory(Category category) { ... }
    // public Optional<Category> findByName(String name) { ... }
    // public void deleteCategory(Long id) { ... }
    // public Category updateCategory(Long id, Category updatedCategory) { ... }
}