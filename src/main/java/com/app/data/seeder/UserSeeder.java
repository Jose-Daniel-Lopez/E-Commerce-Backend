package com.app.data.seeder;

import com.app.entities.Address;
import com.app.entities.Cart;
import com.app.entities.User;
import com.app.repositories.CartRepository;
import com.app.repositories.UserRepository;
import com.github.javafaker.Faker;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Seeder class responsible for populating the database with realistic user and cart data.
 * <p>
 * This class:
 * <ul>
 *   <li>Creates a predefined set of admin, seller, and test users with known credentials</li>
 *   <li>Generates additional customers and sellers using {@link Faker}</li>
 *   <li>Assigns 1–2 shipping addresses per user</li>
 *   <li>Creates shopping carts for users who don't already have one</li>
 *   <li>Uses {@link PasswordEncoder} to securely hash passwords before persistence</li>
 * </ul>
 * <p>
 * Designed to run during application startup. Skips seeding if users or carts already exist.
 * </p>
 */
public class UserSeeder {

    /**
     * Total number of users to generate (including admin, seller, test, and generated users).
     * This ensures a consistent baseline of 50 users for testing and demo purposes.
     */
    private static final int NUM_USERS = 50;

    private final UserRepository userRepo;
    private final CartRepository cartRepo;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker;
    private final Random random = new Random();

    /**
     * Constructs a new UserSeeder with required dependencies.
     *
     * @param userRepo         repository for persisting and retrieving users
     * @param cartRepo         repository for persisting shopping carts
     * @param passwordEncoder  encoder for securely hashing user passwords
     * @param faker            instance for generating realistic dummy data
     */
    public UserSeeder(UserRepository userRepo,
                      CartRepository cartRepo,
                      PasswordEncoder passwordEncoder,
                      Faker faker) {
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.passwordEncoder = passwordEncoder;
        this.faker = faker;
    }

    /**
     * Seeds the database with users and their associated shopping carts if they don't already exist.
     * <p>
     * Executes in two phases:
     * <ol>
     *   <li>{@link #seedUsers()} – Creates admin, seller, test, and synthetic users</li>
     *   <li>{@link #seedCarts()} – Ensures each user has a cart</li>
     * </ol>
     * </p>
     */
    public void seed() {
        if (userRepo.count() == 0) {
            seedUsers();
        }
        if (cartRepo.count() == 0) {
            seedCarts();
        }
    }

    /**
     * Generates and persists a realistic set of users, including:
     * <ul>
     *   <li>One {@link User.Role#ADMIN} with login: <em>admin@techstore.com</em>, password: <em>admin123</em></li>
     *   <li>One {@link User.Role#SELLER} with login: <em>seller@techstore.com</em>, password: <em>seller123</em></li>
     *   <li>One test {@link User.Role#CUSTOMER} with login: <em>test@test.com</em>, password: <em>123456</em></li>
     *   <li>47 additional users (80% CUSTOMER, 20% SELLER) with randomized data</li>
     * </ul>
     * <p>
     * All users are marked as verified and assigned 1–2 addresses in the United States.
     * </p>
     */
    private void seedUsers() {
        System.out.println("Seeding tech users...");

        List<User> users = new ArrayList<>();

        // === PREDEFINED SPECIAL USERS ===
        User admin = new User(
                null,
                "Tech Admin",
                "admin@techstore.com",
                passwordEncoder.encode("admin123"),
                "admin.png",
                User.Role.ADMIN
        );
        admin.setVerified(true);
        admin.addAddress(new Address(
                faker.name().title(),
                faker.address().streetAddress(),
                faker.address().city(),
                faker.address().state(),
                faker.address().zipCode(),
                "United States"
        ));
        users.add(admin);

        User seller = new User(
                null,
                "Tech Seller",
                "seller@techstore.com",
                passwordEncoder.encode("seller123"),
                "seller.png",
                User.Role.SELLER
        );
        seller.setVerified(true);
        seller.addAddress(new Address(
                faker.name().title(),
                faker.address().streetAddress(),
                faker.address().city(),
                faker.address().state(),
                faker.address().zipCode(),
                "United States"
        ));
        users.add(seller);

        User testUser = new User(
                null,
                "test",
                "test@test.com",
                passwordEncoder.encode("123456"),
                "user.png",
                User.Role.CUSTOMER
        );
        testUser.setVerified(true);
        testUser.addAddress(new Address(
                faker.name().title(),
                faker.address().streetAddress(),
                faker.address().city(),
                faker.address().state(),
                faker.address().zipCode(),
                "United States"
        ));
        users.add(testUser);

        // === GENERATED USERS ===
        for (int i = 0; i < NUM_USERS - 3; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            String email = faker.internet().emailAddress(firstName.toLowerCase() + "." + lastName.toLowerCase());

            User.Role role = (i % 5 == 0) ? User.Role.SELLER : User.Role.CUSTOMER;

            User user = new User(null, firstName + " " + lastName, email, passwordEncoder.encode("user123"), "user.png", role);
            user.setVerified(true);

            // Add 1–2 addresses
            int addressCount = random.nextInt(2) + 1;
            for (int j = 0; j < addressCount; j++) {
                user.addAddress(new Address(
                        faker.name().title(),
                        faker.address().streetAddress(),
                        faker.address().city(),
                        faker.address().state(),
                        faker.address().zipCode(),
                        "United States"
                ));
            }

            users.add(user);
        }

        userRepo.saveAll(users);
        System.out.println(users.size() + " tech users created.");
    }

    /**
     * Seeds shopping carts for all users who don't already have one.
     * <p>
     * Each cart is assigned a creation date within the last 30 days to simulate real-world activity.
     * The cart is linked bidirectionally to the user via {@link User#setCart(Cart)}.
     * </p>
     */
    private void seedCarts() {
        System.out.println("Seeding carts...");

        List<Cart> carts = userRepo.findAll().stream()
                .filter(user -> user.getCart() == null) // Only users without carts
                .map(user -> {
                    LocalDateTime createdAt = LocalDateTime.now().minusDays(random.nextInt(30));
                    Cart cart = Cart.builder()
                            .user(user)
                            .createdAt(createdAt)
                            .build();
                    user.setCart(cart); // Maintain bidirectional relationship
                    return cart;
                })
                .collect(Collectors.toList());

        cartRepo.saveAll(carts);
        System.out.println(carts.size() + " carts created.");
    }
}