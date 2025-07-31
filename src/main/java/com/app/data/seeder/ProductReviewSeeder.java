package com.app.data.seeder;

import com.app.entities.Product;
import com.app.entities.ProductReview;
import com.app.entities.User;
import com.app.repositories.ProductRepository;
import com.app.repositories.ProductReviewRepository;
import com.app.repositories.UserRepository;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ProductReviewSeeder {

    private final ProductReviewRepository productReviewRepo;
    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final Faker faker;
    private final Random random = new Random();

    public ProductReviewSeeder(ProductReviewRepository productReviewRepo, ProductRepository productRepo, UserRepository userRepo, Faker faker) {
        this.productReviewRepo = productReviewRepo;
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.faker = faker;
    }

    public void seed() {
        if (productReviewRepo.count() > 0) return;
        System.out.println("Seeding product reviews...");
        List<Product> products = productRepo.findAll();
        List<User> users = userRepo.findAll();
        if (products.isEmpty() || users.isEmpty()) return;

        List<ProductReview> reviews = new ArrayList<>();

        // 3-7 reviews per product
        for (Product product : products) {
            int numReviews = random.nextInt(5) + 3;
            Set<Integer> usedUserIndexes = new HashSet<>();
            for (int i = 0; i < numReviews; i++) {
                // Ensure unique user per review for a product
                int userIdx;
                do {
                    userIdx = random.nextInt(users.size());
                } while (usedUserIndexes.contains(userIdx) && usedUserIndexes.size() < users.size());
                usedUserIndexes.add(userIdx);
                User user = users.get(userIdx);

                ProductReview review = ProductReview.builder()
                        .product(product)
                        .user(user)
                        .rating(random.nextInt(5) + 1) // 1-5 stars
                        .comment(faker.lorem().sentence(faker.number().numberBetween(5, 20)))
                        .createdAt(randomCreatedAt(random))
                        .build();
                reviews.add(review);
            }
        }

        productReviewRepo.saveAll(reviews);
        System.out.println(reviews.size() + " product reviews created.");
    }

    private LocalDateTime randomCreatedAt(Random random) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = LocalDate.now().toEpochDay();
        long randomDay = ThreadLocalRandom.current().longs(startEpochDay, endEpochDay + 1).findAny().orElse(startEpochDay);
        return LocalDateTime.of(LocalDate.ofEpochDay(randomDay), LocalTime.MIN)
                .plusMinutes(random.nextInt(1440));
    }
}

