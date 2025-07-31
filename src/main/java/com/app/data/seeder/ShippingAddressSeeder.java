package com.app.data.seeder;

import com.app.entities.ShippingAddress;
import com.app.entities.User;
import com.app.repositories.ShippingAddressRepository;
import com.app.repositories.UserRepository;
import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Seeder class responsible for populating the database with realistic shipping address data.
 * <p>
 * This class:
 * <ul>
 *   <li>Generates 1–3 unique shipping addresses per user</li>
 *   <li>Uses {@link Faker} to generate plausible street, city, state, and ZIP data</li>
 *   <li>Randomly assigns address titles and types (e.g., Home, Office)</li>
 *   <li>Associates each address with a real user from the database</li>
 *   <li>Assumes all addresses are in the United States (can be extended)</li>
 * </ul>
 * <p>
 * Designed to run during application startup. Skips seeding if addresses already exist.
 * </p>
 */
public class ShippingAddressSeeder {

    private final ShippingAddressRepository shippingAddressRepo;
    private final UserRepository userRepo;
    private final Faker faker;
    private final Random random = new Random();

    /**
     * Constructs a new ShippingAddressSeeder with required dependencies.
     *
     * @param shippingAddressRepo the repository for persisting shipping addresses
     * @param userRepo            the repository to fetch users for address assignment
     * @param faker               the faker instance for generating realistic dummy data
     */
    public ShippingAddressSeeder(ShippingAddressRepository shippingAddressRepo,
                                 UserRepository userRepo,
                                 Faker faker) {
        this.shippingAddressRepo = shippingAddressRepo;
        this.userRepo = userRepo;
        this.faker = faker;
    }

    /**
     * Seeds the database with shipping addresses if none already exist.
     * <p>
     * For each user, creates 1–3 addresses with:
     * <ul>
     *   <li>A descriptive title (e.g., "Riverside Loft")</li>
     *   <li>A random address type ({@link ShippingAddress.AddressType})</li>
     *   <li>Fully generated location data (street, city, state, ZIP)</li>
     *   <li>Fixed country (United States)</li>
     * </ul>
     * </p>
     * <p>
     * Skips execution if shipping addresses already exist or no users are found.
     * </p>
     */
    public void seed() {
        // Skip seeding if addresses already exist
        if (shippingAddressRepo.count() > 0) {
            return;
        }

        System.out.println("Seeding shipping addresses...");

        List<User> users = userRepo.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found. Skipping shipping address seeding.");
            return;
        }

        List<ShippingAddress> addresses = new ArrayList<>();

        // Generate 1–3 addresses per user
        for (User user : users) {
            int addressCount = random.nextInt(3) + 1; // 1 to 3 addresses
            for (int i = 0; i < addressCount; i++) {
                ShippingAddress address = ShippingAddress.builder()
                        .title(generateRandomTitle())
                        .addressType(getRandomAddressType())
                        .street(faker.address().streetAddress())
                        .city(faker.address().city())
                        .state(faker.address().state())
                        .zipCode(faker.address().zipCode())
                        .country("United States") // Fixed for simplicity; can be extended
                        .user(user)
                        .build();
                addresses.add(address);
            }
        }

        // Persist all generated addresses
        shippingAddressRepo.saveAll(addresses);
        System.out.println(addresses.size() + " shipping addresses created for users.");
    }

    /**
     * Generates a random, human-readable title for a shipping address.
     * Combines a location descriptor (e.g., "Beach") with a building type (e.g., "Apartment").
     * Example outputs: "Mountain Studio", "Downtown Office", "Park Building".
     *
     * @return a randomly generated address title
     */
    private String generateRandomTitle() {
        String[] places = {"City", "Downtown", "Uptown", "Riverside", "Mountain", "Beach", "Park", "Center"};
        String[] types = {"Flat", "Apartment", "House", "Office", "Studio", "Loft", "Building", "Complex"};

        String place = places[faker.number().numberBetween(0, places.length)];
        String type = types[faker.number().numberBetween(0, types.length)];
        return place + " " + type;
    }

    /**
     * Selects a random address type from the {@link ShippingAddress.AddressType} enum.
     *
     * @return a randomly chosen address type (e.g., HOME, OFFICE, OTHER)
     */
    private ShippingAddress.AddressType getRandomAddressType() {
        ShippingAddress.AddressType[] types = ShippingAddress.AddressType.values();
        int index = faker.number().numberBetween(0, types.length);
        return types[index];
    }
}