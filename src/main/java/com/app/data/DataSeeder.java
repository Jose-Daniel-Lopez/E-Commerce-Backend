package com.app.data;

import com.app.entities.Address;
import com.app.entities.User;
import com.app.repositories.AddressRepository;
import com.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final AddressRepository addressRepo;

    @Autowired
    public DataSeeder(UserRepository userRepo, AddressRepository addressRepo) {
        this.userRepo = userRepo;
        this.addressRepo = addressRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepo.count() == 0) {
            userRepo.save(new User(null, "Alice Admin", "alice@admin.com", "admin123", "alice.png", User.Role.ADMIN));
            userRepo.save(new User(null, "Bob Seller", "bob@seller.com", "seller123", "bob.png", User.Role.SELLER));
            userRepo.save(new User(null, "Carol Customer", "carol@customer.com", "customer123", "carol.png", User.Role.CUSTOMER));
            userRepo.save(new User(null, "Dave Seller", "dave@seller.com", "davepass", "dave.png", User.Role.SELLER));
            userRepo.save(new User(null, "Eve Customer", "eve@customer.com", "evepass", "eve.png", User.Role.CUSTOMER));
            System.out.println("Placeholder users created in the database.");
        }

        if (addressRepo.count() == 0) {
            addressRepo.save(new Address(null, "123 Main Street", "New York", "NY", "10001"));
            addressRepo.save(new Address(null, "456 Oak Avenue", "Los Angeles", "CA", "90210"));
            addressRepo.save(new Address(null, "789 Pine Road", "Chicago", "IL", "60601"));
            addressRepo.save(new Address(null, "321 Elm Street", "Houston", "TX", "77001"));
            addressRepo.save(new Address(null, "654 Maple Drive", "Phoenix", "AZ", "85001"));
            addressRepo.save(new Address(null, "987 Cedar Lane", "Philadelphia", "PA", "19101"));
            addressRepo.save(new Address(null, "147 Birch Boulevard", "San Antonio", "TX", "78201"));
            addressRepo.save(new Address(null, "258 Willow Way", "San Diego", "CA", "92101"));
            addressRepo.save(new Address(null, "369 Spruce Circle", "Dallas", "TX", "75201"));
            addressRepo.save(new Address(null, "741 Ash Court", "San Jose", "CA", "95101"));
            System.out.println("Placeholder addresses created in the database.");
        }

    }
}
