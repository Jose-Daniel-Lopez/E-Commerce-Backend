package com.app.data;

import com.app.entities.Users;
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
            usersRepository.save(new Users(null, "Alice Admin", "alice@admin.com", "admin123", "alice.png", Users.Role.ADMIN));
            usersRepository.save(new Users(null, "Bob Seller", "bob@seller.com", "seller123", "bob.png", Users.Role.SELLER));
            usersRepository.save(new Users(null, "Carol Customer", "carol@customer.com", "customer123", "carol.png", Users.Role.CUSTOMER));
            usersRepository.save(new Users(null, "Dave Seller", "dave@seller.com", "davepass", "dave.png", Users.Role.SELLER));
            usersRepository.save(new Users(null, "Eve Customer", "eve@customer.com", "evepass", "eve.png", Users.Role.CUSTOMER));
            System.out.println("Placeholder users created in the database.");
        }
    }
}
