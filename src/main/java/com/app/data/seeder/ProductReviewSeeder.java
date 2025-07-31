package com.app.data.seeder;

import com.app.entities.Product;
import com.app.entities.ProductReview;
import com.app.entities.User;
import com.app.repositories.ProductReviewRepository;
import com.app.repositories.ProductRepository;
import com.app.repositories.UserRepository;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Seeder class responsible for populating the database with realistic product review data.
 * <p>
 * This class uses {@link Faker} to generate synthetic but meaningful review content such as
 * comments and timestamps. It ensures that each product receives 3–7 unique user reviews,
 * with no duplicate users per product.
 * </p>
 * <p>
 * Designed to be used during application startup or data initialization.
 * Skips seeding if reviews already exist in the database.
 * </p>
 */
public class ProductReviewSeeder {

    private final ProductReviewRepository productReviewRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final Faker faker;
    private final Random random = new Random();

    /**
     * Constructs a new ProductReviewSeeder with required dependencies.
     *
     * @param productReviewRepo the repository for persisting product reviews
     * @param productRepo       the repository to fetch products for review assignment
     * @param userRepo          the repository to fetch users who will write reviews
     * @param faker             the faker instance for generating realistic dummy data
     */
    public ProductReviewSeeder(ProductReviewRepository productReviewRepo,
                               ProductRepository productRepo,
                               UserRepository userRepo,
                               Faker faker) {
        this.productReviewRepo = productReviewRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.faker = faker;
    }

    /**
     * Seeds the database with product reviews if none already exist.
     * <p>
     * For each product, creates 3–7 reviews from unique users. Each review includes:
     * <ul>
     *   <li>A randomly selected user (no duplicates per product)</li>
     *   <li>A rating between 1 and 5 stars</li>
     *   <li>A generated comment using lorem ipsum-like text</li>
     *   <li>A creation timestamp within the last 30 days</li>
     * </ul>
     * </p>
     * <p>
     * If there are no products or users in the system, seeding is skipped.
     * </p>
     */
    public void seed() {
        // Skip seeding if reviews already exist
        if (productReviewRepo.count() > 0) {
            return;
        }

        System.out.println("Seeding product reviews...");

        List<Product> products = productRepo.findAll();
        List<User> users = userRepo.findAll();

        // Ensure there are products and users to work with
        if (products.isEmpty() || users.isEmpty()) {
            System.out.println("Skipping review seeding: Not enough data (products or users missing).");
            return;
        }

        List<ProductReview> reviews = new ArrayList<>();

        // Generate 3–7 reviews for each product
        for (Product product : products) {
            int numReviews = random.nextInt(5) + 3; // 3 to 7 reviews
            Set<Integer> usedUserIndexes = new HashSet<>();

            for (int i = 0; i < numReviews; i++) {
                // Select a unique user for each review on this product
                int userIdx;
                do {
                    userIdx = random.nextInt(users.size());
                } while (usedUserIndexes.contains(userIdx) && usedUserIndexes.size() < users.size());

                // Break loop if all users have been used
                if (usedUserIndexes.size() >= users.size()) {
                    break;
                }

                usedUserIndexes.add(userIdx);
                User user = users.get(userIdx);

                ProductReview review = ProductReview.builder()
                        .product(product)
                        .user(user)
                        .rating(random.nextInt(5) + 1) // Rating from 1 to 5
                        .comment(faker.lorem().sentence(faker.number().numberBetween(5, 20)))
                        .createdAt(randomCreatedAt(random))
                        .build();

                reviews.add(review);
            }
        }

        // Persist all generated reviews
        productReviewRepo.saveAll(reviews);
        System.out.println(reviews.size() + " product reviews created.");
    }

    /**
     * Generates a random {@link LocalDateTime} within the last 30 days.
     * The time of day is also randomized (between 00:00 and 23:59).
     *
     * @param random the random number generator instance
     * @return a random timestamp between 30 days ago and now
     */
    private LocalDateTime randomCreatedAt(Random random) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = LocalDate.now().toEpochDay();

        // Pick a random day in the range
        long randomDay = ThreadLocalRandom.current().longs(startEpochDay, endEpochDay + 1)
                .findAny()
                .orElse(startEpochDay);

        // Combine with a random time of day (0–1439 minutes)
        return LocalDateTime.of(LocalDate.ofEpochDay(randomDay), LocalTime.MIN)
                .plusMinutes(random.nextInt(1440)); // 1440 minutes in a day
    }
}