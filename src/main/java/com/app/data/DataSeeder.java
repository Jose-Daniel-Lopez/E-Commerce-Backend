package com.app.data;

import com.app.entities.Address;
import com.app.entities.Order;
import com.app.entities.Payment;
import com.app.entities.User;
import com.app.repositories.OrderRepository;
import com.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final OrderRepository orderRepo;

    @Autowired
    public DataSeeder(UserRepository userRepo, OrderRepository orderRepo) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        // --- Seed Users and Addresses ---
        if (userRepo.count() == 0) {
            // Alice Admin - 2 addresses
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

            // Save users (addresses are saved automatically by cascade)
            userRepo.save(alice);
            userRepo.save(bob);
            userRepo.save(carol);
            userRepo.save(dave);
            userRepo.save(eve);

            System.out.println("Placeholder users and addresses created successfully!");
        }

        // --- Seed Orders and Payments ---
        if (orderRepo.count() == 0) {
            // Order 1: CREATED -> PENDING Payment
            Order order1 = Order.builder()
                    .orderDate(LocalDateTime.of(2025, 6, 20, 10, 30))
                    .status(Order.Status.CREATED)
                    .totalAmount(new BigDecimal("150.75"))
                    .build();
            Payment payment1 = Payment.builder()
                    .paymentMethod("Credit Card")
                    .status(Payment.Status.PENDING)
                    .build();
            order1.setPayment(payment1);

            // Order 2: PAID -> COMPLETED Payment
            Order order2 = Order.builder()
                    .orderDate(LocalDateTime.of(2025, 6, 19, 15, 45))
                    .status(Order.Status.PAID)
                    .totalAmount(new BigDecimal("320.00"))
                    .build();
            Payment payment2 = Payment.builder()
                    .paymentMethod("PayPal")
                    .status(Payment.Status.COMPLETED)
                    .build();
            order2.setPayment(payment2);

            // Order 3: SHIPPED -> COMPLETED Payment
            Order order3 = Order.builder()
                    .orderDate(LocalDateTime.of(2025, 6, 18, 9, 0))
                    .status(Order.Status.SHIPPED)
                    .totalAmount(new BigDecimal("89.99"))
                    .build();
            Payment payment3 = Payment.builder()
                    .paymentMethod("Credit Card")
                    .status(Payment.Status.COMPLETED)
                    .build();
            order3.setPayment(payment3);

            // Order 4: DELIVERED -> COMPLETED Payment
            Order order4 = Order.builder()
                    .orderDate(LocalDateTime.of(2025, 6, 17, 14, 20))
                    .status(Order.Status.DELIVERED)
                    .totalAmount(new BigDecimal("450.50"))
                    .build();
            Payment payment4 = Payment.builder()
                    .paymentMethod("Bank Transfer")
                    .status(Payment.Status.COMPLETED)
                    .build();
            order4.setPayment(payment4);

            // Order 5: CANCELED -> FAILED Payment
            Order order5 = Order.builder()
                    .orderDate(LocalDateTime.of(2025, 6, 16, 11, 10))
                    .status(Order.Status.CANCELED)
                    .totalAmount(new BigDecimal("75.00"))
                    .build();
            Payment payment5 = Payment.builder()
                    .paymentMethod("Credit Card")
                    .status(Payment.Status.FAILED)
                    .build();
            order5.setPayment(payment5);

            // Save orders (payments are saved automatically by cascade)
            orderRepo.save(order1);
            orderRepo.save(order2);
            orderRepo.save(order3);
            orderRepo.save(order4);
            orderRepo.save(order5);

            System.out.println("Placeholder orders and their payments created in the database.");
        }
    }
}
