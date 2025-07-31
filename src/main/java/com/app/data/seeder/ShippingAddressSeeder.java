package com.app.data.seeder;

import com.app.entities.ShippingAddress;
import com.app.entities.User;
import com.app.repositories.ShippingAddressRepository;
import com.app.repositories.UserRepository;
import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ShippingAddressSeeder {

    private final ShippingAddressRepository shippingAddressRepo;
    private final UserRepository userRepo;
    private final Faker faker;
    private final Random random = new Random();

    public ShippingAddressSeeder(ShippingAddressRepository shippingAddressRepo, UserRepository userRepo, Faker faker) {
        this.shippingAddressRepo = shippingAddressRepo;
        this.userRepo = userRepo;
        this.faker = faker;
    }

    public void seed() {
        if (shippingAddressRepo.count() > 0) return;
        System.out.println("Seeding shipping addresses...");
        List<User> users = userRepo.findAll();
        if (users.isEmpty()) return;
        List<ShippingAddress> addresses = new ArrayList<>();
        for (User user : users) {
            int addressCount = random.nextInt(3) + 1;
            for (int i = 0; i < addressCount; i++) {
                addresses.add(ShippingAddress.builder()
                        .title(generateRandomTitle())
                        .addressType(getRandomAddressType())
                        .street(faker.address().streetAddress())
                        .city(faker.address().city())
                        .state(faker.address().state())
                        .zipCode(faker.address().zipCode())
                        .country("United States")
                        .user(user)
                        .build());
            }
        }
        shippingAddressRepo.saveAll(addresses);
        System.out.println(addresses.size() + " shipping addresses created for users.");
    }

    private String generateRandomTitle() {
        String[] places = {"City", "Downtown", "Uptown", "Riverside", "Mountain", "Beach", "Park", "Center"};
        String[] types = {"Flat", "Apartment", "House", "Office", "Studio", "Loft", "Building", "Complex"};
        return places[faker.number().numberBetween(0, places.length)] + " " +
                types[faker.number().numberBetween(0, types.length)];
    }

    private ShippingAddress.AddressType getRandomAddressType() {
        ShippingAddress.AddressType[] types = ShippingAddress.AddressType.values();
        return types[faker.number().numberBetween(0, types.length)];
    }
}

