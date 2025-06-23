package com.app.data;

import com.app.entities.*;
import com.app.repositories.DiscountCodeRepository;
import com.app.repositories.OrderRepository;
import com.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final DiscountCodeRepository discountCodeRepo; // New repository

    @Autowired
    public DataSeeder(UserRepository userRepo, OrderRepository orderRepo, DiscountCodeRepository discountCodeRepo) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.discountCodeRepo = discountCodeRepo; // Inject in constructor
    }

    @Override
    public void run(String... args) throws Exception {
        // --- Seed Users and Addresses (if database is empty) ---
        if (userRepo.count() == 0) {
            seedUsers();
        }

        // --- Seed Discount Codes (if database is empty) ---
        if (discountCodeRepo.count() == 0) {
            seedDiscountCodes();
        }

        // --- Seed Orders, Payments and link Discount Codes (if database is empty) ---
        if (orderRepo.count() == 0) {
            seedOrdersAndPayments();
        }
    }

    private void seedUsers() {
        // Alice Admin - 2 addresses
        User alice = new User(null, "Alice Admin", "alice@admin.com", "admin123", "alice.png", User.Role.ADMIN);
        alice.addAddress(new Address("123 Main Street", "New York", "NY", "10001", "USA"));
        alice.addAddress(new Address("456 Oak Avenue", "Los Angeles", "CA", "90210", "USA"));

        // Bob Seller - 2 addresses
        User bob = new User(null, "Bob Seller", "bob@seller.com", "seller123", "bob.png", User.Role.SELLER);
        bob.addAddress(new Address("789 Pine Road", "Chicago", "IL", "60601", "USA"));
        bob.addAddress(new Address("321 Elm Street", "Houston", "TX", "77001", "USA"));

        // ... (other users) ...

        userRepo.saveAll(List.of(alice, bob /*, ... other users */));
        System.out.println("Placeholder users and addresses created successfully!");
    }

    private void seedDiscountCodes() {
        DiscountCode summer25 = DiscountCode.builder()
                .code("SUMMER25")
                .discountAmount(new BigDecimal("25.00"))
                .expiryDate(LocalDate.now().plusMonths(3))
                .isActive(true)
                .build();

        DiscountCode save10 = DiscountCode.builder()
                .code("SAVE10")
                .discountAmount(new BigDecimal("10.00"))
                .expiryDate(LocalDate.now().plusYears(1))
                .isActive(true)
                .build();

        DiscountCode expired = DiscountCode.builder()
                .code("WINTER_EXPIRED")
                .discountAmount(new BigDecimal("50.00"))
                .expiryDate(LocalDate.now().minusDays(1))
                .isActive(true) // Still active, but date is past
                .build();

        DiscountCode inactive = DiscountCode.builder()
                .code("INACTIVE")
                .discountAmount(new BigDecimal("5.00"))
                .expiryDate(LocalDate.now().plusYears(1))
                .isActive(false)
                .build();

        discountCodeRepo.saveAll(List.of(summer25, save10, expired, inactive));
        System.out.println("Placeholder discount codes created.");
    }

    private void seedOrdersAndPayments() {
        // Fetch the discount codes to be used
        DiscountCode summerDiscount = discountCodeRepo.findByCode("SUMMER25").orElse(null);
        DiscountCode save10Discount = discountCodeRepo.findByCode("SAVE10").orElse(null);

        // Order 1: CREATED -> PENDING Payment, with a discount
        Order order1 = Order.builder()
                .orderDate(LocalDateTime.now().minusDays(3))
                .status(Order.Status.CREATED)
                .totalAmount(new BigDecimal("150.75"))
                .discountCode(summerDiscount) // Assign discount code
                .build();
        order1.setPayment(new Payment("Credit Card", Payment.Status.PENDING));

        // Order 2: PAID -> COMPLETED Payment, with a discount
        Order order2 = Order.builder()
                .orderDate(LocalDateTime.now().minusDays(2))
                .status(Order.Status.PAID)
                .totalAmount(new BigDecimal("320.00"))
                .discountCode(save10Discount) // Assign discount code
                .build();
        order2.setPayment(new Payment("PayPal", Payment.Status.COMPLETED));

        // Order 3: SHIPPED -> COMPLETED Payment, no discount
        Order order3 = Order.builder()
                .orderDate(LocalDateTime.now().minusDays(1))
                .status(Order.Status.SHIPPED)
                .totalAmount(new BigDecimal("89.99"))
                .discountCode(null) // Explicitly no discount
                .build();
        order3.setPayment(new Payment("Credit Card", Payment.Status.COMPLETED));

        // Order 4: DELIVERED -> COMPLETED Payment, no discount
        Order order4 = Order.builder()
                .orderDate(LocalDateTime.now().minusDays(5))
                .status(Order.Status.DELIVERED)
                .totalAmount(new BigDecimal("450.50"))
                .build();
        order4.setPayment(new Payment("Bank Transfer", Payment.Status.COMPLETED));

        // Order 5: CANCELED -> FAILED Payment, no discount
        Order order5 = Order.builder()
                .orderDate(LocalDateTime.now().minusDays(10))
                .status(Order.Status.CANCELED)
                .totalAmount(new BigDecimal("75.00"))
                .build();
        order5.setPayment(new Payment("Credit Card", Payment.Status.FAILED));

        orderRepo.saveAll(List.of(order1, order2, order3, order4, order5));
        System.out.println("Placeholder orders, payments, and discount links created.");
    }
}
