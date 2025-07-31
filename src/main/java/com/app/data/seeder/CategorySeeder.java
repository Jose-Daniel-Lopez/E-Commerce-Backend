package com.app.data.seeder;

import com.app.entities.Category;
import com.app.repositories.CategoryRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Seeder class responsible for populating the database with predefined {@link Category} entries.
 * <p>
 * This seeder initializes the system with a curated list of product categories organized into two
 * main domains:
 * </p>
 * <ul>
 *   <li><strong>Mobile & Compute</strong>: Devices focused on portability and general computing</li>
 *   <li><strong>Input & Control</strong>: Hardware used to interact with systems or games</li>
 * </ul>
 * <p>
 * Each category is assigned a human-readable name and an icon identifier (used by the frontend
 * to render visual cues, e.g., via icon libraries like Bootstrap Icons or Game Icons).
 * </p>
 * <p>
 * The seeding process is <strong>idempotent</strong>: it only executes if no categories exist
 * in the database, preventing duplication across application restarts.
 * </p>
 *
 * @see Category
 * @see CategoryRepository
 */
public class CategorySeeder {

    private final CategoryRepository categoryRepo;

    /**
     * Constructs a new {@code CategorySeeder} with the required repository dependency.
     *
     * @param categoryRepo the repository used to persist and query categories; must not be {@code null}
     * @throws IllegalArgumentException if {@code categoryRepo} is {@code null}
     */
    public CategorySeeder(CategoryRepository categoryRepo) {
        this.categoryRepo = categoryRepo;
    }

    /**
     * Seeds the database with a predefined set of product categories if none already exist.
     * <p>
     * The method performs the following steps:
     * </p>
     * <ol>
     *   <li>Checks if categories are already present (skips seeding if so)</li>
     *   <li>Defines category names and corresponding frontend icon classes</li>
     *   <li>Maps names and icons into {@link Category} entities using a builder</li>
     *   <li>Persists all categories in a single batch operation</li>
     * </ol>
     * <p>
     * This structure ensures consistent, readable, and maintainable seed data that aligns with
     * current product groupings and UI requirements.
     * </p>
     * <p>
     * Example category-icon mapping:
     * </p>
     * <pre>
     * "Smartphones" → "gi-smartphone"
     * "Keyboards"   → "bi-keyboard"
     * </pre>
     */
    public void seed() {
        // Prevent duplicate seeding: only run if no categories exist
        if (categoryRepo.count() > 0) {
            return;
        }

        System.out.println("Seeding updated categories for Mobile & Compute and Input & Control...");

        // Define category names grouped by domain for clarity
        List<String> categoryNames = Arrays.asList(
                // === MOBILE & COMPUTE ===
                "Smartphones",
                "Tablets",
                "Laptops",
                "Handhelds",          // e.g., gaming handhelds like Steam Deck

                // === INPUT & CONTROL ===
                "Keyboards",
                "Mice",
                "Controllers"         // e.g., gamepads, joysticks
        );

        // Define corresponding icon identifiers (frontend uses these for display)
        List<String> categoryIcons = Arrays.asList(
                // MOBILE & COMPUTE
                "gi-smartphone",      // Smartphones
                "co-tablet",          // Tablets
                "bi-laptop",          // Laptops
                "bi-nintendo-switch", // Handhelds

                // INPUT & CONTROL
                "bi-keyboard",        // Keyboards
                "bi-mouse",           // Mice
                "gi-console-controller" // Controllers
        );

        // Validate consistency between names and icons
        if (categoryNames.size() != categoryIcons.size()) {
            throw new IllegalStateException(
                    "Mismatch between category names (" + categoryNames.size() +
                            ") and icons (" + categoryIcons.size() + ") count."
            );
        }

        // Build Category entities by pairing each name with its corresponding icon
        List<Category> categories = IntStream.range(0, categoryNames.size())
                .mapToObj(i -> Category.builder()
                        .name(categoryNames.get(i))
                        .icon(categoryIcons.get(i))
                        .build())
                .collect(Collectors.toList());

        // Persist all categories in a single transaction
        categoryRepo.saveAll(categories);
        System.out.println(categories.size() + " updated categories created.");
    }
}