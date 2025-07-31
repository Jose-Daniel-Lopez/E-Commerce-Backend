package com.app.data.seeder;

import com.app.entities.DiscountCode;
import com.app.repositories.DiscountCodeRepository;
import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiscountCodeSeeder {

    private static final int NUM_DISCOUNT_CODES = 10;

    private final DiscountCodeRepository discountCodeRepo;
    private final Faker faker;
    private final Random random = new Random();

    public DiscountCodeSeeder(DiscountCodeRepository discountCodeRepo, Faker faker) {
        this.discountCodeRepo = discountCodeRepo;
        this.faker = faker;
    }

    public void seed() {
        if (discountCodeRepo.count() > 0) return;
        System.out.println("Seeding discount codes...");
        List<DiscountCode> discountCodes = new ArrayList<>();

        for (int i = 0; i < NUM_DISCOUNT_CODES; i++) {
            DiscountCode discountCode = DiscountCode.builder()
                    .code(faker.lorem().word().toUpperCase() + faker.number().numberBetween(10, 99))
                    .discountAmount(faker.number().numberBetween(5, 25)) // Changed from discountPercentage to discountAmount
                    .expiryDate(LocalDate.now().plusDays(faker.number().numberBetween(30, 365))) // Changed to LocalDate
                    .isActive(random.nextBoolean())
                    .build();
            discountCodes.add(discountCode);
        }

        discountCodeRepo.saveAll(discountCodes);
        System.out.println(discountCodes.size() + " discount codes created.");
    }
}

