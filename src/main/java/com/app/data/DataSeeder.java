package com.app.data;

import com.app.data.seeder.*;
import com.app.repositories.*;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

/**
 * A {@link CommandLineRunner} component that orchestrates the database seeding process.
 * <p>
 * This class delegates the seeding of individual entities to specialized seeder classes,
 * ensuring a clean and modularized data generation process. It maintains the correct
 * seeding order to respect foreign key constraints.
 * </p>
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private final UserSeeder userSeeder;
    private final CategorySeeder categorySeeder;
    private final ProductSeeder productSeeder;
    private final OrderSeeder orderSeeder;
    private final DiscountCodeSeeder discountCodeSeeder;
    private final ShippingAddressSeeder shippingAddressSeeder;
    private final CartItemSeeder cartItemSeeder;
    private final ProductReviewSeeder productReviewSeeder;
    private final WishlistSeeder wishlistSeeder;

    @Autowired
    public DataSeeder(
            UserRepository userRepo,
            OrderRepository orderRepo,
            DiscountCodeRepository discountCodeRepo,
            PaymentRepository paymentRepo,
            CartRepository cartRepo,
            CartItemRepository cartItemRepo,
            ProductReviewRepository productReviewRepo,
            ProductRepository productRepo,
            CategoryRepository categoryRepo,
            OrderItemRepository orderItemRepo,
            ProductVariantRepository productVariantRepo,
            ShippingAddressRepository shippingAddressRepo,
            WishlistRepository wishlistRepo,
            PasswordEncoder passwordEncoder
    ) {
        Faker faker = new Faker(Locale.ENGLISH);

        this.userSeeder = new UserSeeder(userRepo, cartRepo, passwordEncoder, faker);
        this.categorySeeder = new CategorySeeder(categoryRepo);
        this.productSeeder = new ProductSeeder(productRepo, categoryRepo, productVariantRepo, faker);
        this.orderSeeder = new OrderSeeder(orderRepo, orderItemRepo, paymentRepo, userRepo, productVariantRepo);
        this.discountCodeSeeder = new DiscountCodeSeeder(discountCodeRepo, faker);
        this.shippingAddressSeeder = new ShippingAddressSeeder(shippingAddressRepo, userRepo, faker);
        this.cartItemSeeder = new CartItemSeeder(cartItemRepo, cartRepo, productVariantRepo);
        this.productReviewSeeder = new ProductReviewSeeder(productReviewRepo, productRepo, userRepo, faker);
        this.wishlistSeeder = new WishlistSeeder(wishlistRepo, userRepo, productRepo);
    }

    /**
     * Executes the data seeding process by calling specialized seeders in order.
     */
    @Override
    @Transactional
    public void run(String... args) {
        System.out.println("Starting tech e-commerce data seeding process...");

        userSeeder.seed();
        categorySeeder.seed();
        productSeeder.seed();
        discountCodeSeeder.seed();
        orderSeeder.seed();
        shippingAddressSeeder.seed();
        cartItemSeeder.seed();
        productReviewSeeder.seed();
        wishlistSeeder.seed();

        System.out.println("Tech e-commerce data seeding completed successfully!");
    }
}
