package com.app.data;

import com.app.entities.Address;
import com.app.entities.User;
import com.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;

    @Autowired
    public DataSeeder(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepo.count() == 0) {
            // Alice Admin - 2 adresses
            User alice = new User(null, "Alice Admin", "alice@admin.com", "admin123", "alice.png", User.Role.ADMIN);
            alice.addAddress(new Address("123 Main Street", "New York", "NY", "10001", "USA"));
            alice.addAddress(new Address("456 Oak Avenue", "Los Angeles", "CA", "90210", "USA"));

            // Bob Seller - 2 addresses
            User bob = new User(null, "Bob Seller", "bob@seller.com", "seller123", "bob.png", User.Role.SELLER);
            bob.addAddress(new Address("789 Pine Road", "Chicago", "IL", "60601", "USA"));
            bob.addAddress(new Address("321 Elm Street", "Houston", "TX", "77001", "USA"));

            // Carol Customer - 3 addresses
            User carol = new User(null, "Carol Customer", "carol@customer.com", "customer123", "carol.png", User.Role.CUSTOMER);
            carol.addAddress(new Address("654 Maple Drive", "Phoenix", "AZ", "85001", "USA"));
            carol.addAddress(new Address("987 Cedar Lane", "Philadelphia", "PA", "19101", "USA"));
            carol.addAddress(new Address("147 Birch Boulevard", "San Antonio", "TX", "78201", "USA"));

            // Dave Seller - 1 address
            User dave = new User(null, "Dave Seller", "dave@seller.com", "davepass", "dave.png", User.Role.SELLER);
            dave.addAddress(new Address("258 Willow Way", "San Diego", "CA", "92101", "USA"));

            // Eve Customer - 2 addresses
            User eve = new User(null, "Eve Customer", "eve@customer.com", "evepass", "eve.png", User.Role.CUSTOMER);
            eve.addAddress(new Address("369 Spruce Circle", "Dallas", "TX", "75201", "USA"));
            eve.addAddress(new Address("741 Ash Court", "San Jose", "CA", "95101", "USA"));

            // Save users
            userRepo.save(alice);
            userRepo.save(bob);
            userRepo.save(carol);
            userRepo.save(dave);
            userRepo.save(eve);

            // Print confirmation
            System.out.println("Users with their addresses created in the database:");
            System.out.println("- Alice Admin: 2 addresses");
            System.out.println("- Bob Seller: 2 addresses");
            System.out.println("- Carol Customer: 3 addresses");
            System.out.println("- Dave Seller: 1 address");
            System.out.println("- Eve Customer: 2 addresses");
        }
    }
}
