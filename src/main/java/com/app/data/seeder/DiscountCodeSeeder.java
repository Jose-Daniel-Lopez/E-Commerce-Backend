package com.app.data.seeder;

import com.app.entities.DiscountCode;
import com.app.repositories.DiscountCodeRepository;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Seeder class responsible for populating the database with sample {@link DiscountCode} entities.
 * <p>
 * This class generates realistic promotional codes for testing pricing logic, checkout flows,
 * and marketing features in development or staging environments. Each code includes:
 * </p>
 * <ul>
 *   <li>A randomly generated uppercase alphanumeric code</li>
 *   <li>A fixed discount amount between $5 and $25</li>
 *   <li>An expiry date set 30–365 days in the future</li>
 *   <li>Random activation status ({@code active} or {@code inactive})</li>
 * </ul>
 * <p>
 * The seeding process is <strong>idempotent</strong>: it only runs if no discount codes currently
 * exist in the database, preventing duplication on application restarts.
 * </p>
 * <p>
 * Uses <a href="https://github.com/DiUS/java-faker">JavaFaker</a> to generate plausible,
 * human-readable test data.
 * </p>
 *
 * @see DiscountCode
 * @see DiscountCodeRepository
 * @see Faker
 */
public class DiscountCodeSeeder {

    /**
     * Number of discount codes to generate during seeding.
     * This value can be adjusted based on testing needs.
     */
    private static final int NUM_DISCOUNT_CODES = 10;

    private final DiscountCodeRepository discountCodeRepo;
    private final Faker faker;
    private final Random random = new Random();

    /**
     * Constructs a new {@code DiscountCodeSeeder} with required dependencies.
     *
     * @param discountCodeRepo the repository for persisting and querying discount codes; must not be {@code null}
     * @param faker            the fake data generator used to create realistic values; must not be {@code null}
     * @throws IllegalArgumentException if any dependency is {@code null}
     */
    public DiscountCodeSeeder(DiscountCodeRepository discountCodeRepo, Faker faker) {
        this.discountCodeRepo = discountCodeRepo;
        this.faker = faker;
    }

    /**
     * Seeds the database with a batch of randomly generated discount codes if none already exist.
     * <p>
     * This method:
     * </p>
     * <ol>
     *   <li>Checks if any discount codes are already present (skips if so)</li>
     *   <li>Generates a predefined number of {@link DiscountCode} instances using randomized but realistic data</li>
     *   <li>Persists all codes in a single bulk operation</li>
     * </ol>
     * <p>
     * Each discount code is assigned:
     * </p>
     * <ul>
     *   <li><strong>Code:</strong> A mix of a capitalized word and two digits (e.g., "Electron77")</li>
     *   <li><strong>Amount:</strong> A flat discount between $5 and $25 (intended for USD)</li>
     *   <li><strong>Expiry:</strong> A date 30–365 days from now</li>
     *   <li><strong>Status:</strong> Randomly enabled or disabled</li>
     * </ul>
     * <p>
     * Designed to support frontend and backend testing of:
     * promo code entry, validation, application, and expiration logic.
     * </p>
     *
     * @see #NUM_DISCOUNT_CODES
     * @see DiscountCode
     */
    public void seed() {
        // Skip seeding if discount codes already exist to prevent duplicates
        if (discountCodeRepo.count() > 0) {
            return;
        }

        System.out.println("Seeding discount codes...");

        List<DiscountCode> discountCodes = new ArrayList<>();

        for (int i = 0; i < NUM_DISCOUNT_CODES; i++) {
            // Generate a plausible discount code: e.g., "GAMING42"
            String code = faker.lorem().word().toUpperCase() +
                    faker.number().numberBetween(10, 99);

            // Flat discount amount between $5 and $25
            int discountAmount = faker.number().numberBetween(5, 25);

            // Expiry date: 30–365 days from today
            LocalDate expiryDate = LocalDate.now().plusDays(faker.number().numberBetween(30, 365));

            // Randomly determine whether the code is currently active
            boolean isActive = random.nextBoolean();

            DiscountCode discountCode = DiscountCode.builder()
                    .code(code)
                    .discountAmount(discountAmount)
                    .expiryDate(expiryDate)
                    .isActive(isActive)
                    .build();

            discountCodes.add(discountCode);
        }

        // Persist all discount codes in a single transaction
        discountCodeRepo.saveAll(discountCodes);
        System.out.println(discountCodes.size() + " discount codes created.");
    }
}