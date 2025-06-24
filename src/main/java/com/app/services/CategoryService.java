package com.app.services;

import com.app.repositories.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    // Repository for Category entity
    private final CategoryRepository categoryRepo;

    // Constructor-based dependency injection for CategoryRepository
    @Autowired
    public CategoryService(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }
}
