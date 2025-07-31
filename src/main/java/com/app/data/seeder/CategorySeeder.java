package com.app.data.seeder;

import com.app.entities.Category;
import com.app.repositories.CategoryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class CategorySeeder {

    private final CategoryRepository categoryRepo;

    public CategorySeeder(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    public void seed() {
        if (categoryRepo.count() > 0) return;
        System.out.println("Seeding updated categories for Mobile & Compute and Input & Control...");

        List<String> categoryNames = Arrays.asList(
                // === MOBILE & COMPUTE ===
                "Smartphones",
                "Tablets",
                "Laptops",
                "Handhelds",          // e.g., gaming handhelds like Steam Deck

                // === INPUT & CONTROL ===
                "Keyboards",
                "Mice",
                "Controllers"        // e.g., gamepads, joysticks
        );

        List<String> categoryIcons = Arrays.asList(
                // MOBILE & COMPUTE
                "gi-smartphone",      // Smartphones
                "co-tablet",          // Tablets
                "bi-laptop",          // Laptops
                "bi-nintendo-switch",      // Handhelds

                // INPUT & CONTROL
                "bi-keyboard",        // Keyboards
                "bi-mouse",           // Mice
                "gi-console-controller" // Controllers
        );

        List<Category> categories = IntStream.range(0, categoryNames.size())
                .mapToObj(i -> Category.builder()
                        .name(categoryNames.get(i))
                        .icon(categoryIcons.get(i))
                        .build())
                .collect(Collectors.toList());

        categoryRepo.saveAll(categories);
        System.out.println(categories.size() + " updated categories created.");
    }
}

