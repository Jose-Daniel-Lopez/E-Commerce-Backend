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
    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductReviewRepository productReviewRepo;
    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductVariantRepository productVariantRepo;

    // Constructor injection for repositories
    @Autowired
    public DataSeeder(UserRepository userRepo, OrderRepository orderRepo, DiscountCodeRepository discountCodeRepo, CartRepository cartRepo, CartItemRepository cartItemRepo, ProductReviewRepository productReviewRepo, ProductRepository productRepo, CategoryRepository categoryRepo, OrderItemRepository orderItemRepo, ProductVariantRepository productVariantRepo) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.discountCodeRepo = discountCodeRepo;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.productReviewRepo = productReviewRepo;
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.orderItemRepo = orderItemRepo;
        this.productVariantRepo = productVariantRepo;
    }

    // This method is called when the application starts
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepo.count() == 0) {
            seedUsersWithCartsAndAddresses();
        }
        if (categoryRepo.count() == 0) {
            seedCategories();
        }
        if (productRepo.count() == 0) {
            seedProducts();
        }
        if (productVariantRepo.count() == 0) {  // After products
            seedProductVariants();
        }
        if (discountCodeRepo.count() == 0) {
            seedDiscountCodes();
        }
        if (orderRepo.count() == 0) {
            seedOrdersAndPayments();
        }
        if (orderItemRepo.count() == 0) {
            seedOrderItems();
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
        if (orderRepo.count() > 0) {
            return;
        }

        List<User> users = userRepo.findAll();

        if (users.isEmpty()) {
            System.out.println("No users found. Skipping orders seeding.");
            return;
        }

        List<Order> orders = new ArrayList<>();

        // Create multiple orders for different users
        for (User user : users) {
            // Each user gets 1-3 orders
            int orderCount = (int) (Math.random() * 3) + 1;

            for (int i = 0; i < orderCount; i++) {
                Order order = Order.builder()
                        .orderDate(LocalDateTime.now().minusDays((int) (Math.random() * 30)))
                        .status(Order.Status.values()[(int) (Math.random() * Order.Status.values().length)])
                        .totalAmount(new BigDecimal("0.00")) // Will be calculated later
                        .user(user) // Assign the user to the order
                        .build();

                orders.add(order);
            }
        }

        orderRepo.saveAll(orders);
        System.out.println("Sample orders created: " + orders.size() + " orders for " + users.size() + " users.");
    }


    // Seed sample order items
    private void seedOrderItems() {
        if (orderItemRepo.count() > 0) {
            return;
        }

        List<Order> orders = orderRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();

        if (orders.isEmpty() || variants.isEmpty()) {
            System.out.println("No orders or product variants found. Skipping order items seeding.");
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();

        for (Order order : orders) {
            int itemCount = (int) (Math.random() * 3) + 2;

            for (int i = 0; i < itemCount; i++) {
                ProductVariant randomVariant = variants.get((int) (Math.random() * variants.size()));
                int quantity = (int) (Math.random() * 5) + 1;
                BigDecimal unitPrice = randomVariant.getProduct().getBasePrice();

                OrderItem orderItem = OrderItem.builder()
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .order(order)
                        .productVariant(randomVariant)
                        .build();

                orderItems.add(orderItem);
            }
        }

        orderItemRepo.saveAll(orderItems);
        System.out.println("Sample order items created: " + orderItems.size() + " items.");
    }

    // Seed cart items for each user based on their role
    private void seedCartItems() {
        if (cartItemRepo.count() > 0) {
            return;
        }

        List<Cart> carts = cartRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();

        if (carts.isEmpty() || variants.isEmpty()) {
            System.out.println("No carts or product variants found. Skipping cart items seeding.");
            return;
        }

        List<CartItem> cartItems = new ArrayList<>();

        // Ensure every cart gets at least one item
        for (Cart cart : carts) {
            // Add 1-3 random variants to each cart
            int itemCount = (int) (Math.random() * 3) + 1;

            System.out.println("Creating " + itemCount + " items for cart ID: " + cart.getId());

            for (int i = 0; i < itemCount; i++) {
                ProductVariant randomVariant = variants.get((int) (Math.random() * variants.size()));
                int quantity = (int) (Math.random() * 3) + 1;

                CartItem cartItem = CartItem.builder()
                        .quantity(quantity)
                        .cart(cart)  // Make sure this is set
                        .productVariant(randomVariant)  // Make sure this is set
                        .build();

                cartItems.add(cartItem);

                System.out.println("Created CartItem: cartId=" + cart.getId() +
                        ", variantId=" + randomVariant.getId() +
                        ", quantity=" + quantity);
            }
        }

        cartItemRepo.saveAll(cartItems);
        System.out.println("Sample cart items created: " + cartItems.size() + " items for " + carts.size() + " carts.");

        // Verify the data was saved
        for (Cart cart : carts) {
            long itemCount = cartItemRepo.countByCartId(cart.getId());
            System.out.println("Cart ID " + cart.getId() + " has " + itemCount + " items");
        }
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

        // Get existing categories (they should be created first)
        List<Category> allCategories = categoryRepo.findAll();

        // Find specific categories by name
        Category electronics = allCategories.stream()
                .filter(cat -> cat.getName().equals("Electronics"))
                .findFirst().orElse(null);

        Category computers = allCategories.stream()
                .filter(cat -> cat.getName().equals("Computers"))
                .findFirst().orElse(null);

        Category audio = allCategories.stream()
                .filter(cat -> cat.getName().equals("Audio"))
                .findFirst().orElse(null);

        Product product1 = Product.builder()
                .name("Smartphone X")
                .description("Latest generation smartphone with OLED display.")
                .basePrice(new BigDecimal("699.99"))
                .totalStock(100)
                .category(electronics)  // Assign single category
                .build();

        Product product2 = Product.builder()
                .name("Pro Headphones")
                .description("Wireless headphones with noise cancellation.")
                .basePrice(new BigDecimal("199.99"))
                .totalStock(250)
                .category(audio)  // Assign single category
                .build();

        Product product3 = Product.builder()
                .name("Ultra Laptop")
                .description("Ultralight laptop with high-performance processor.")
                .basePrice(new BigDecimal("1299.99"))
                .totalStock(50)
                .category(computers)  // Assign single category
                .build();

        Product product4 = Product.builder()
                .name("Gaming Mouse")
                .description("High-precision gaming mouse with RGB lighting.")
                .basePrice(new BigDecimal("79.99"))
                .totalStock(150)
                .category(electronics)  // Multiple products can share same category
                .build();

        // Save all products to the database
        productRepo.saveAll(List.of(product1, product2, product3, product4));
        System.out.println("Sample products created with categories assigned.");
    }


    // Seed sample categories
    private void seedCategories() {
        // Check if categories already exist to avoid duplicate seeding
        if (categoryRepo.count() > 0) {
            return;
        }

        Category electronics = Category.builder()
                .name("Electronics")
                .products(new ArrayList<>())
                .build();

        Category computers = Category.builder()
                .name("Computers")
                .products(new ArrayList<>())
                .build();

        Category audio = Category.builder()
                .name("Audio")
                .products(new ArrayList<>())
                .build();

        Category mobile = Category.builder()
                .name("Mobile Devices")
                .products(new ArrayList<>())
                .build();

        // Save all categories to the database
        categoryRepo.saveAll(List.of(electronics, computers, audio, mobile));
        System.out.println("Sample categories created.");
    }

    // Seed sample product variants
    private void seedProductVariants() {
        if (productVariantRepo.count() > 0) {
            return;
        }

        List<Product> products = productRepo.findAll();

        if (products.isEmpty()) {
            System.out.println("No products found. Skipping product variants seeding.");
            return;
        }

        List<ProductVariant> variants = new ArrayList<>();

        for (Product product : products) {
            switch (product.getName()) {
                case "Smartphone X" -> {
                    variants.add(createVariant(product, "128GB", "Black", 25, "SMX-128-BLK-001"));
                    variants.add(createVariant(product, "256GB", "White", 20, "SMX-256-WHT-002"));
                    variants.add(createVariant(product, "512GB", "Blue", 15, "SMX-512-BLU-003"));
                }
                case "Pro Headphones" -> {
                    variants.add(createVariant(product, "Standard", "Black", 50, "PHD-STD-BLK-001"));
                    variants.add(createVariant(product, "Standard", "Silver", 30, "PHD-STD-SLV-002"));
                }
                case "Ultra Laptop" -> {
                    variants.add(createVariant(product, "8GB/256GB", "Space Gray", 10, "ULT-8256-SG-001"));
                    variants.add(createVariant(product, "16GB/512GB", "Space Gray", 8, "ULT-16512-SG-002"));
                    variants.add(createVariant(product, "16GB/1TB", "Silver", 5, "ULT-161TB-SLV-003"));
                }
                case "Gaming Mouse" -> {
                    variants.add(createVariant(product, "Standard", "Black", 40, "GMO-STD-BLK-001"));
                    variants.add(createVariant(product, "RGB", "Multi", 35, "GMO-RGB-MLT-002"));
                }
                default -> {
                    variants.add(createVariant(product, "Standard", "Default", 20,
                            generateSKU(product.getName(), "STD", "DEF")));
                }
            }
        }

        productVariantRepo.saveAll(variants);
        System.out.println("Sample product variants created: " + variants.size() + " variants.");
    }

    // Helper methods
    private ProductVariant createVariant(Product product, String size, String color, Integer stock, String sku) {
        return ProductVariant.builder()
                .size(size)
                .color(color)
                .stock(stock)
                .sku(sku)
                .product(product)
                .build();
    }

    private String generateSKU(String productName, String size, String color) {
        String prefix = productName.substring(0, Math.min(3, productName.length())).toUpperCase();
        String sizeCode = size.substring(0, Math.min(3, size.length())).toUpperCase();
        String colorCode = color.substring(0, Math.min(3, color.length())).toUpperCase();
        return String.format("%s-%s-%s-%03d", prefix, sizeCode, colorCode, (int)(Math.random() * 999) + 1);
    }
}