package com.app.data;

import com.app.entities.*;
import com.app.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final DiscountCodeRepository discountCodeRepo;
    private final CartItemRepository cartItemRepo;

    @Autowired
    public DataSeeder(UserRepository userRepo, OrderRepository orderRepo,
                      DiscountCodeRepository discountCodeRepo, CartItemRepository cartItemRepo) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.discountCodeRepo = discountCodeRepo;
        this.cartItemRepo = cartItemRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Seed Users, Addresses and Carts
        if (userRepo.count() == 0) {
            seedUsersWithCarts();
        }

        // Seed Discount Codes
        if (discountCodeRepo.count() == 0) {
            seedDiscountCodes();
        }

        // Seed Orders and Payments
        if (orderRepo.count() == 0) {
            seedOrdersAndPayments();
        }

        // Seed Cart Items (after carts exist)
        if (cartItemRepo.count() == 0) {
            seedCartItems();
        }
    }

    private void seedUsersWithCarts() {
        // Alice Admin - 2 addresses + cart
        User alice = new User(null, "Alice Admin", "alice@admin.com", "admin123", "alice.png", User.Role.ADMIN);
        alice.addAddress(new Address("123 Main Street", "New York", "NY", "10001", "USA"));
        alice.addAddress(new Address("456 Oak Avenue", "Los Angeles", "CA", "90210", "USA"));

        Cart aliceCart = Cart.builder()
                .createdAt(LocalDateTime.now().minusDays(5))
                .build();
        alice.setCart(aliceCart);

        // Bob Seller - 2 addresses + cart
        User bob = new User(null, "Bob Seller", "bob@seller.com", "seller123", "bob.png", User.Role.SELLER);
        bob.addAddress(new Address("789 Pine Road", "Chicago", "IL", "60601", "USA"));
        bob.addAddress(new Address("321 Elm Street", "Houston", "TX", "77001", "USA"));

        Cart bobCart = Cart.builder()
                .createdAt(LocalDateTime.now().minusDays(3))
                .build();
        bob.setCart(bobCart);

        // Carol Customer - 3 addresses + cart
        User carol = new User(null, "Carol Customer", "carol@customer.com", "customer123", "carol.png", User.Role.CUSTOMER);
        carol.addAddress(new Address("654 Maple Drive", "Phoenix", "AZ", "85001", "USA"));
        carol.addAddress(new Address("987 Cedar Lane", "Philadelphia", "PA", "19101", "USA"));
        carol.addAddress(new Address("147 Birch Boulevard", "San Antonio", "TX", "78201", "USA"));

        Cart carolCart = Cart.builder()
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();
        carol.setCart(carolCart);

        // Dave Seller - 1 address + cart
        User dave = new User(null, "Dave Seller", "dave@seller.com", "davepass", "dave.png", User.Role.SELLER);
        dave.addAddress(new Address("258 Willow Way", "San Diego", "CA", "92101", "USA"));

        Cart daveCart = Cart.builder()
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();
        dave.setCart(daveCart);

        // Eve Customer - 2 addresses + cart
        User eve = new User(null, "Eve Customer", "eve@customer.com", "evepass", "eve.png", User.Role.CUSTOMER);
        eve.addAddress(new Address("369 Spruce Circle", "Dallas", "TX", "75201", "USA"));
        eve.addAddress(new Address("741 Ash Court", "San Jose", "CA", "95101", "USA"));

        Cart eveCart = Cart.builder()
                .createdAt(LocalDateTime.now())
                .build();
        eve.setCart(eveCart);

        // Save users (addresses and carts are saved automatically by cascade)
        userRepo.saveAll(List.of(alice, bob, carol, dave, eve));
        System.out.println("Placeholder users with addresses and carts created successfully!");
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
                .isActive(true)
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
        // Fetch discount codes
        DiscountCode summerDiscount = discountCodeRepo.findByCode("SUMMER25").orElse(null);
        DiscountCode save10Discount = discountCodeRepo.findByCode("SAVE10").orElse(null);

        // Create orders with payments
        Order order1 = Order.builder()
                .orderDate(LocalDateTime.now().minusDays(3))
                .status(Order.Status.CREATED)
                .totalAmount(new BigDecimal("150.75"))
                .discountCode(summerDiscount)
                .build();
        order1.setPayment(Payment.builder()
                .paymentMethod("Credit Card")
                .status(Payment.Status.PENDING)
                .build());

        Order order2 = Order.builder()
                .orderDate(LocalDateTime.now().minusDays(2))
                .status(Order.Status.PAID)
                .totalAmount(new BigDecimal("320.00"))
                .discountCode(save10Discount)
                .build();
        order2.setPayment(Payment.builder()
                .paymentMethod("PayPal")
                .status(Payment.Status.COMPLETED)
                .build());

        Order order3 = Order.builder()
                .orderDate(LocalDateTime.now().minusDays(1))
                .status(Order.Status.SHIPPED)
                .totalAmount(new BigDecimal("89.99"))
                .build();
        order3.setPayment(Payment.builder()
                .paymentMethod("Credit Card")
                .status(Payment.Status.COMPLETED)
                .build());

        Order order4 = Order.builder()
                .orderDate(LocalDateTime.now().minusDays(5))
                .status(Order.Status.DELIVERED)
                .totalAmount(new BigDecimal("450.50"))
                .build();
        order4.setPayment(Payment.builder()
                .paymentMethod("Bank Transfer")
                .status(Payment.Status.COMPLETED)
                .build());

        Order order5 = Order.builder()
                .orderDate(LocalDateTime.now().minusDays(10))
                .status(Order.Status.CANCELED)
                .totalAmount(new BigDecimal("75.00"))
                .build();
        order5.setPayment(Payment.builder()
                .paymentMethod("Credit Card")
                .status(Payment.Status.FAILED)
                .build());

        orderRepo.saveAll(List.of(order1, order2, order3, order4, order5));
        System.out.println("Placeholder orders, payments, and discount links created.");
    }

    // Seed Cart Items
    private void seedCartItems() {
        // Fetch all existing carts
        List<User> users = userRepo.findAll();

        for (User user : users) {
            if (user.getCart() != null) {
                Cart cart = user.getCart();

                // Create different numbers of cart items based on user role
                switch (user.getRole()) {
                    case ADMIN:
                        // Admin has 1 item (minimal cart)
                        cart.addCartItem(CartItem.builder()
                                .quantity(1)
                                .build());
                        break;

                    case SELLER:
                        // Sellers have 2 items (moderate cart)
                        cart.addCartItem(CartItem.builder()
                                .quantity(3)
                                .build());
                        cart.addCartItem(CartItem.builder()
                                .quantity(1)
                                .build());
                        break;

                    case CUSTOMER:
                        // Customers have 3-4 items (full cart)
                        cart.addCartItem(CartItem.builder()
                                .quantity(2)
                                .build());
                        cart.addCartItem(CartItem.builder()
                                .quantity(5)
                                .build());
                        cart.addCartItem(CartItem.builder()
                                .quantity(1)
                                .build());

                        // Some customers have an extra item
                        if (user.getName().contains("Carol")) {
                            cart.addCartItem(CartItem.builder()
                                    .quantity(3)
                                    .build());
                        }
                        break;
                }

                // Save the cart (cart items are saved by cascade)
                userRepo.save(user);
            }
        }

        System.out.println("Cart items created for all existing carts:");
        System.out.println("- Admin users: 1 item per cart");
        System.out.println("- Seller users: 2 items per cart");
        System.out.println("- Customer users: 3-4 items per cart");
    }
}
