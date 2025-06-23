package com.app.data;

import com.app.entities.User;
import com.app.repositories.UsersRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UsersRepository usersRepository;

    public DataSeeder(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (usersRepository.count() == 0) {
            usersRepository.save(new User(null, "Alice Admin", "alice@admin.com", "admin123", "alice.png", User.Role.ADMIN));
            usersRepository.save(new User(null, "Bob Seller", "bob@seller.com", "seller123", "bob.png", User.Role.SELLER));
            usersRepository.save(new User(null, "Carol Customer", "carol@customer.com", "customer123", "carol.png", User.Role.CUSTOMER));
            usersRepository.save(new User(null, "Dave Seller", "dave@seller.com", "davepass", "dave.png", User.Role.SELLER));
            usersRepository.save(new User(null, "Eve Customer", "eve@customer.com", "evepass", "eve.png", User.Role.CUSTOMER));
            System.out.println("Placeholder users created in the database.");
        }
    }
}
