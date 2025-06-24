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
import java.util.ArrayList;
import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    // Repositories for accessing the database
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final DiscountCodeRepository discountCodeRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductReviewRepository productReviewRepo;
    private final ProductRepository productRepo;

    // Constructor injection for repositories
    @Autowired
    public DataSeeder(
            UserRepository userRepo,
            OrderRepository orderRepo,
            DiscountCodeRepository discountCodeRepo,
            CartItemRepository cartItemRepo,
            ProductReviewRepository productReviewRepo,
            ProductRepository productRepo
    ) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.discountCodeRepo = discountCodeRepo;
        this.cartItemRepo = cartItemRepo;
        this.productReviewRepo = productReviewRepo;
        this.productRepo = productRepo;
    }

    // This method is called when the application starts
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepo.count() == 0) {
            seedUsersWithCartsAndAddresses();
        }
        if (productRepo.count() == 0) {
            seedProducts();
        }
        if (discountCodeRepo.count() == 0) {
            seedDiscountCodes();
        }
        if (orderRepo.count() == 0) {
            seedOrdersAndPayments();
        }
        if (cartItemRepo.count() == 0) {
            seedCartItems();
        }
        if (productReviewRepo.count() == 0) {
            seedProductReviews();
        }
    }

    // Seed users with addresses and carts
    private void seedUsersWithCartsAndAddresses() {
        // Create users with addresses and carts
        User alice = new User(null, "Alice Admin", "alice@admin.com", "admin123", "alice.png", User.Role.ADMIN);
        alice.addAddress(new Address("123 Main Street", "New York", "NY", "10001", "USA"));
        alice.addAddress(new Address("456 Oak Avenue", "Los Angeles", "CA", "90210", "USA"));
        Cart aliceCart = Cart.builder().createdAt(LocalDateTime.now().minusDays(5)).build();
        alice.setCart(aliceCart);

        User bob = new User(null, "Bob Seller", "bob@seller.com", "seller123", "bob.png", User.Role.SELLER);
        bob.addAddress(new Address("789 Pine Road", "Chicago", "IL", "60601", "USA"));
        bob.addAddress(new Address("321 Elm Street", "Houston", "TX", "77001", "USA"));
        Cart bobCart = Cart.builder().createdAt(LocalDateTime.now().minusDays(3)).build();
        bob.setCart(bobCart);

        User carol = new User(null, "Carol Customer", "carol@customer.com", "customer123", "carol.png", User.Role.CUSTOMER);
        carol.addAddress(new Address("654 Maple Drive", "Phoenix", "AZ", "85001", "USA"));
        carol.addAddress(new Address("987 Cedar Lane", "Philadelphia", "PA", "19101", "USA"));
        carol.addAddress(new Address("147 Birch Boulevard", "San Antonio", "TX", "78201", "USA"));
        Cart carolCart = Cart.builder().createdAt(LocalDateTime.now().minusDays(1)).build();
        carol.setCart(carolCart);

        User dave = new User(null, "Dave Seller", "dave@seller.com", "davepass", "dave.png", User.Role.SELLER);
        dave.addAddress(new Address("258 Willow Way", "San Diego", "CA", "92101", "USA"));
        Cart daveCart = Cart.builder().createdAt(LocalDateTime.now().minusDays(2)).build();
        dave.setCart(daveCart);

        User eve = new User(null, "Eve Customer", "eve@customer.com", "evepass", "eve.png", User.Role.CUSTOMER);
        eve.addAddress(new Address("369 Spruce Circle", "Dallas", "TX", "75201", "USA"));
        eve.addAddress(new Address("741 Ash Court", "San Jose", "CA", "95101", "USA"));
        Cart eveCart = Cart.builder().createdAt(LocalDateTime.now()).build();
        eve.setCart(eveCart);

        userRepo.saveAll(List.of(alice, bob, carol, dave, eve));
        System.out.println("Users with addresses and carts created.");
    }

    // Seed discount codes
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
        System.out.println("Discount codes created.");
    }

    // Seed orders and payments
    private void seedOrdersAndPayments() {
        DiscountCode summerDiscount = discountCodeRepo.findByCode("SUMMER25").orElse(null);
        DiscountCode save10Discount = discountCodeRepo.findByCode("SAVE10").orElse(null);

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
        System.out.println("Orders and payments created.");
    }

    // Seed cart items for each user based on their role
    private void seedCartItems() {
        List<User> users = userRepo.findAll();
        for (User user : users) {
            Cart cart = user.getCart();
            if (cart != null) {
                // Example: Different number of items per user
                if (user.getRole() == User.Role.ADMIN) {
                    cart.addCartItem(CartItem.builder().quantity(1).build());
                } else if (user.getRole() == User.Role.SELLER) {
                    cart.addCartItem(CartItem.builder().quantity(2).build());
                    cart.addCartItem(CartItem.builder().quantity(1).build());
                } else if (user.getRole() == User.Role.CUSTOMER) {
                    cart.addCartItem(CartItem.builder().quantity(2).build());
                    cart.addCartItem(CartItem.builder().quantity(5).build());
                    cart.addCartItem(CartItem.builder().quantity(1).build());
                    if (user.getName().contains("Carol")) {
                        cart.addCartItem(CartItem.builder().quantity(3).build());
                    }
                }
                // Save cart (cart items are saved by cascade)
                // If you have a CartRepository, use it; otherwise, userRepo.save(user) is enough
                // cartRepo.save(cart);
                userRepo.save(user);
            }
        }
        System.out.println("Cart items created.");
    }

    // Seed product reviews for each user
    private void seedProductReviews() {
        List<User> users = userRepo.findAll();
        List<Product> products = productRepo.findAll(); // Get all products
        List<ProductReview> reviews = new ArrayList<>();

        for (User user : users) {
            int reviewCount = switch (user.getName()) {
                case "Alice Admin" -> 2;
                case "Bob Seller" -> 1;
                case "Carol Customer" -> 3;
                default -> 1;
            };

            for (int i = 1; i <= reviewCount; i++) {
                // Assign a random product to each review
                Product randomProduct = products.get((int) (Math.random() * products.size()));

                ProductReview review = ProductReview.builder()
                        .rating((int) (Math.random() * 5) + 1)
                        .comment("Review #" + i + " by " + user.getName())
                        .createdAt(LocalDateTime.now().minusDays(i))
                        .user(user)
                        .product(randomProduct)  // Set the product reference
                        .build();
                reviews.add(review);
            }
        }
        productReviewRepo.saveAll(reviews);
        System.out.println("Product reviews created.");
    }

    // Seed sample products
    private void seedProducts() {
        // Check if products already exist to avoid duplicate seeding
        if (productRepo.count() > 0) {
            return;
        }

        Product product1 = Product.builder()
                .name("Smartphone X")
                .description("Latest generation smartphone with OLED display.")
                .basePrice(new BigDecimal("699.99"))
                .totalStock(100)
                .build();

        Product product2 = Product.builder()
                .name("Pro Headphones")
                .description("Wireless headphones with noise cancellation.")
                .basePrice(new BigDecimal("199.99"))
                .totalStock(250)
                .build();

        Product product3 = Product.builder()
                .name("Ultra Laptop")
                .description("Ultralight laptop with high-performance processor.")
                .basePrice(new BigDecimal("1299.99"))
                .totalStock(50)
                .build();

        // Save all products to the database
        productRepo.saveAll(List.of(product1, product2, product3));
        System.out.println("Sample products created.");
    }
}