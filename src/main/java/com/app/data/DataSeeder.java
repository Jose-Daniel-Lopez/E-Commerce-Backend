package com.app.data;

import com.app.entities.*;
import com.app.repositories.*;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class DataSeeder implements CommandLineRunner {

    // --- Configuration for Phase 1 Data Load ---
    private static final int NUM_USERS = 50;
    private static final int NUM_PRODUCTS_PER_CATEGORY = 15;
    private static final int NUM_ORDERS = 50;
    private static final int NUM_DISCOUNT_CODES = 10;
    private static final int NUM_REVIEWS = 70;

    // Technology brands for products
    private static final List<String> TECH_BRANDS = Arrays.asList(
            "Apple", "Samsung", "Xiaomi", "Google", "Huawei", "OnePlus",
            "Sony", "LG", "Motorola", "Oppo", "Vivo", "Realme", "Honor",
            "Nothing", "Asus", "Nokia", "TCL", "Fairphone", "RedMagic",
            "Dell", "HP", "Lenovo", "Microsoft", "Acer", "MSI", "Razer",
            "Logitech", "Corsair", "SteelSeries", "HyperX", "Alienware"
    );

    // Tech product names by category
    private static final Map<String, List<String>> PRODUCT_NAMES = createProductNamesMap();

    private static Map<String, List<String>> createProductNamesMap() {
        Map<String, List<String>> map = new HashMap<>();
        map.put("Smartphones", Arrays.asList("Pro Max", "Ultra", "Edge", "Note", "Pixel", "Find X", "Mi", "Galaxy", "iPhone", "Xperia"));
        map.put("Smartwatches", Arrays.asList("Watch", "Fit", "Active", "Classic", "Sport", "SE", "Ultra", "Pro"));
        map.put("Cameras", Arrays.asList("Alpha", "EOS", "Z", "X-T", "GH", "D", "R", "FX", "Lumix", "Cyber-shot"));
        map.put("Headphones", Arrays.asList("WH", "QuietComfort", "Studio", "Pro", "Max", "Elite", "Momentum", "HD", "ATH", "DT"));
        map.put("Computers", Arrays.asList("MacBook", "ThinkPad", "XPS", "Spectre", "ZenBook", "Pavilion", "IdeaPad", "Surface", "Legion", "ROG"));
        map.put("Keyboards", Arrays.asList("MX Keys", "K", "Huntsman", "BlackWidow", "Alloy", "G", "Pro X", "Das Keyboard", "WASD", "Ducky"));
        map.put("Mice", Arrays.asList("MX Master", "G Pro", "DeathAdder", "Basilisk", "Model", "Pulsefire", "Rival", "EC", "FK", "ZA"));
        map.put("Gaming", Arrays.asList("Call of Duty", "FIFA", "Assassin's Creed", "The Witcher", "Cyberpunk", "God of War", "Spider-Man", "Horizon", "Elden Ring", "Halo"));
        map.put("Tablets", Arrays.asList("iPad", "Galaxy Tab", "Surface", "MatePad", "Tab", "Pixel Slate", "Fire", "Mi Pad", "Tab S", "Pro"));
        map.put("Smart Home", Arrays.asList("Echo", "Nest", "Ring", "Philips Hue", "TP-Link Kasa", "SmartThings", "HomeKit", "Alexa", "Google Home", "Wyze"));
        map.put("Audio", Arrays.asList("HomePod", "Echo Studio", "Sonos", "JBL", "Bose", "Bang & Olufsen", "KEF", "Klipsch", "Yamaha", "Denon"));
        map.put("Accessories", Arrays.asList("MagSafe", "USB-C", "Lightning", "Wireless Charger", "Power Bank", "Stand", "Case", "Screen Protector", "Cable", "Adapter"));
        return map;
    }

    // Repositories for accessing the database
    private final UserRepository userRepo;
    private final OrderRepository orderRepo;
    private final DiscountCodeRepository discountCodeRepo;
    private final PaymentRepository paymentRepo;
    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final ProductReviewRepository productReviewRepo;
    private final ProductRepository productRepo;
    private final CategoryRepository categoryRepo;
    private final OrderItemRepository orderItemRepo;
    private final ProductVariantRepository productVariantRepo;
    private final ShippingAddressRepository shippingAddressRepo;

    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker(Locale.ENGLISH);
    private final Random random = new Random();

    @Autowired
    public DataSeeder(UserRepository userRepo, OrderRepository orderRepo, DiscountCodeRepository discountCodeRepo, PaymentRepository paymentRepo, CartRepository cartRepo, CartItemRepository cartItemRepo, ProductReviewRepository productReviewRepo, ProductRepository productRepo, CategoryRepository categoryRepo, OrderItemRepository orderItemRepo, ProductVariantRepository productVariantRepo, PasswordEncoder passwordEncoder, ShippingAddressRepository shippingAddressRepo) {
        this.userRepo = userRepo;
        this.orderRepo = orderRepo;
        this.discountCodeRepo = discountCodeRepo;
        this.paymentRepo = paymentRepo;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.productReviewRepo = productReviewRepo;
        this.productRepo = productRepo;
        this.categoryRepo = categoryRepo;
        this.orderItemRepo = orderItemRepo;
        this.productVariantRepo = productVariantRepo;
        this.passwordEncoder = passwordEncoder;
        this.shippingAddressRepo = shippingAddressRepo;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Starting tech e-commerce data seeding process...");

        // The order of seeding is crucial due to entity dependencies.
        if (userRepo.count() == 0) seedUsers();
        if (cartRepo.count() == 0) seedCarts();
        if (categoryRepo.count() == 0) seedCategories();
        if (productRepo.count() == 0) seedProducts();
        if (productVariantRepo.count() == 0) seedProductVariants();
        if (discountCodeRepo.count() == 0) seedDiscountCodes();
        if (orderRepo.count() == 0) seedOrders(); // Orders are created before items, payments, and addresses
        if (orderItemRepo.count() == 0) seedOrderItemsAndUpdateTotals(); // This method now also updates order totals
        if (paymentRepo.count() == 0) seedPayments();
        if (shippingAddressRepo.count() == 0) seedShippingAddresses();
        if (cartItemRepo.count() == 0) seedCartItems();
        if (productReviewRepo.count() == 0) seedProductReviews();

        System.out.println("Tech e-commerce data seeding completed successfully!");
    }

    private void seedUsers() {
        if (userRepo.count() > 0) return;
        System.out.println("Seeding tech users...");
        List<User> users = new ArrayList<>();

        // Create some fixed users for testing purposes
        User admin = new User(null, "Tech Admin", "admin@techstore.com", passwordEncoder.encode("admin123"), "admin.png", User.Role.ADMIN);
        admin.addAddress(new Address(faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
        users.add(admin);

        User seller = new User(null, "Tech Seller", "seller@techstore.com", passwordEncoder.encode("seller123"), "seller.png", User.Role.SELLER);
        seller.addAddress(new Address(faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
        users.add(seller);

        // Generate the rest of the users with tech-focused names
        for (int i = 0; i < NUM_USERS - 2; i++) {
            String firstName = faker.name().firstName();
            String lastName = faker.name().lastName();
            User user = new User(
                    null,
                    firstName + " " + lastName,
                    faker.internet().emailAddress(firstName.toLowerCase() + "." + lastName.toLowerCase()),
                    passwordEncoder.encode("user123"),
                    "user.png",
                    i % 5 == 0 ? User.Role.SELLER : User.Role.CUSTOMER
            );
            // Add 1 to 2 addresses for each user
            int addressCount = random.nextInt(2) + 1;
            for (int j = 0; j < addressCount; j++) {
                user.addAddress(new Address(faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
            }
            users.add(user);
        }
        userRepo.saveAll(users);
        System.out.println(users.size() + " tech users created.");
    }

    private void seedCarts() {
        if (cartRepo.count() > 0) return;
        System.out.println("Seeding carts...");
        List<User> users = userRepo.findAll();
        List<Cart> carts = users.stream()
                .filter(user -> user.getCart() == null)
                .map(user -> {
                    Cart cart = Cart.builder()
                            .user(user)
                            .createdAt(LocalDateTime.now().minusDays(random.nextInt(30)))
                            .build();
                    user.setCart(cart);
                    return cart;
                }).collect(Collectors.toList());
        cartRepo.saveAll(carts);
        System.out.println(carts.size() + " carts created.");
    }

    private void seedCategories() {
        if (categoryRepo.count() > 0) return;
        System.out.println("Seeding categories...");
        List<String> categoryNames = Arrays.asList(
            "Smartphones",
            "Smartwatches",
            "Cameras",
            "Headphones",
            "Computers",
            "Keyboards",
            "Mice",
            "Gaming",
            "Tablets",
            "Smart Home",
            "Audio",
            "Accessories"
        );

        List<String> categoryIcons = Arrays.asList(
                "gi-smartphone",
                "bi-smartwatch",
                "bi-camera",
                "la-headphones-solid",
                "bi-laptop",
                "bi-keyboard",
                "bi-mouse",
                "gi-console-controller",
                "co-tablet",
                "ri-home-wifi-line",
                "hi-music-note",
                "md-cable"
        );

        List<Category> categories = IntStream.range(0, categoryNames.size())
                .mapToObj(i -> Category.builder()
                        .name(categoryNames.get(i))
                        .icon(categoryIcons.get(i))
                        .build())
                .collect(Collectors.toList());
        categoryRepo.saveAll(categories);
        System.out.println(categories.size() + " categories created.");
    }

    private void seedProducts() {
        if (productRepo.count() > 0) return;
        System.out.println("Seeding products...");
        List<Category> categories = categoryRepo.findAll();
        if (categories.isEmpty()) {
            System.out.println("No categories found. Skipping product seeding.");
            return;
        }
        List<Product> products = new ArrayList<>();
        for (Category category : categories) {
            for (int i = 0; i < NUM_PRODUCTS_PER_CATEGORY; i++) {
                boolean isFeatured = random.nextBoolean() && i < 3;

                Product.ProductBuilder productBuilder = Product.builder()
                        .name(faker.commerce().productName())
                        .brand(TECH_BRANDS.get(random.nextInt(TECH_BRANDS.size())))
                        .description(faker.lorem().sentence(10))
                        .isFeatured(isFeatured)
                        .basePrice(new BigDecimal(faker.commerce().price(5.00, 1500.00)).setScale(2, RoundingMode.HALF_UP))
                        .totalStock(0)
                        .category(category);

                switch (category.getName()) {
                    case "Smartphones":
                        productBuilder
                                .screenSize(faker.options().option("6.1\"", "6.5\"", "6.7\""))
                                .cpu(faker.options().option("A16 Bionic", "Snapdragon 8 Gen 2", "Tensor G2"))
                                .memoria(faker.options().option("128GB", "256GB", "512GB"))
                                .numberOfCores(new Integer[]{8, 10, 12}[random.nextInt(3)])
                                .camera(faker.options().option("48MP", "50MP", "108MP"))
                                .frontCamera(faker.options().option("12MP", "16MP"))
                                .battery(faker.options().option("4000mAh", "4500mAh", "5000mAh"));
                        break;
                    case "Smartwatches":
                        productBuilder
                                .displaySize(faker.options().option("1.4\"", "1.6\"", "1.9\""))
                                .batteryLife(faker.options().option("18 hours", "24 hours", "3 days"))
                                .waterResistance(faker.options().option("5 ATM", "IP68"))
                                .connectivity(faker.options().option("Bluetooth, Wi-Fi, Cellular", "Bluetooth, Wi-Fi"))
                                .healthSensors(faker.options().option("Heart Rate, SpO2, ECG", "Heart Rate, SpO2"))
                                .compatibility(faker.options().option("iOS", "Android", "iOS & Android"));
                        break;
                    case "Cameras":
                        productBuilder
                                .resolution(faker.options().option("24MP", "33MP", "61MP"))
                                .sensorType(faker.options().option("Full-Frame CMOS", "APS-C", "Micro Four Thirds"))
                                .lensMount(faker.options().option("Sony E-Mount", "Canon RF", "Nikon Z"))
                                .videoResolution(faker.options().option("4K 60fps", "8K 30fps", "1080p 120fps"))
                                .isoRange(faker.options().option("100-51200", "100-32000"))
                                .opticalZoom(faker.options().option("3x", "5x", "10x"));
                        break;
                    case "Headphones":
                        productBuilder
                                .driverSize(faker.options().option("40mm", "50mm"))
                                .frequencyResponse(faker.options().option("20Hz - 20kHz", "15Hz - 25kHz"))
                                .impedance(faker.options().option("32 Ohm", "64 Ohm"))
                                .noiseCancel(random.nextBoolean())
                                .bluetoothVersion(faker.options().option("5.0", "5.2", "5.3"));
                        break;
                    case "Computers":
                        productBuilder
                                .processorModel(faker.options().option("Intel Core i7", "AMD Ryzen 9", "Apple M2 Pro"))
                                .ramCapacity(new Integer[]{16, 32, 64}[random.nextInt(3)])
                                .storageType(faker.options().option("SSD", "NVMe SSD"))
                                .storageCapacity(new Integer[]{512, 1024, 2048}[random.nextInt(3)])
                                .graphicsCard(faker.options().option("NVIDIA RTX 4070", "AMD Radeon RX 7800 XT", "Integrated"))
                                .operatingSystem(faker.options().option("Windows 11", "macOS Ventura", "Linux"));
                        break;
                    case "Keyboards":
                        productBuilder
                                .keyType(faker.options().option("Mechanical", "Membrane"))
                                .layout(faker.options().option("QWERTY", "AZERTY"))
                                .backlight(faker.options().option("RGB", "White", "None"))
                                .keyProfile(faker.options().option("High Profile", "Low Profile"))
                                .ergonomic(random.nextBoolean());
                        break;
                    case "Mice":
                        productBuilder
                                .dpi(new Integer[]{8000, 12000, 16000}[random.nextInt(3)])
                                .programmableButtons(random.nextBoolean());
                        break;
                    case "Gaming":
                        productBuilder
                                .platform(faker.options().option("PlayStation 5", "Xbox Series X", "PC", "Nintendo Switch"))
                                .gameGenre(faker.options().option("RPG", "Action", "Shooter", "Strategy"))
                                .playerCount(new Integer[]{1, 2, 4}[random.nextInt(3)])
                                .onlineMultiplayer(random.nextBoolean())
                                .systemRequirements(faker.lorem().sentence(5))
                                .ageRating(faker.options().option("PEGI 18", "PEGI 12", "Everyone"));
                        break;
                    case "Tablets":
                        productBuilder
                                .screenSize(faker.options().option("10.2\"", "11\"", "12.9\""))
                                .resolution(faker.options().option("2160x1620", "2388x1668", "2732x2048"))
                                .storageCapacity(new Integer[]{64, 128, 256}[random.nextInt(3)])
                                .ramCapacity(new Integer[]{4, 8, 16}[random.nextInt(3)])
                                .operatingSystem(faker.options().option("iPadOS", "Android"))
                                .batteryLife(faker.options().option("10 hours", "12 hours"));
                        break;
                    case "Smart Home":
                        productBuilder
                                .powerSource(faker.options().option("Battery", "Plug-in"))
                                .controlMethod(faker.options().option("App", "Voice Assistant", "Remote"))
                                .automationFeatures(faker.lorem().sentence(3))
                                .securityFeatures(faker.lorem().sentence(3));
                        break;
                    case "Audio":
                        productBuilder
                                .powerOutput(new Integer[]{20, 50, 100}[random.nextInt(3)])
                                .speakerConfiguration(faker.options().option("2.1", "5.1", "Stereo"));
                        break;
                    case "Accessories":
                        productBuilder
                                .material(faker.commerce().material())
                                .dimensions(String.format("%d x %d x %d cm", random.nextInt(20) + 1, random.nextInt(20) + 1, random.nextInt(5) + 1))
                                .weight(String.format("%d g", random.nextInt(500) + 50))
                                .warranty(faker.options().option("1 year", "2 years", "Lifetime"))
                                .color(faker.color().name());
                        break;
                }
                products.add(productBuilder.build());
            }
        }
        productRepo.saveAll(products);
        System.out.println(products.size() + " products created.");
    }

    private void seedProductVariants() {
        if (productVariantRepo.count() > 0) return;
        System.out.println("Seeding product variants...");
        List<Product> products = productRepo.findAll();
        if (products.isEmpty()) {
            System.out.println("No products found. Skipping variant seeding.");
            return;
        }
        List<ProductVariant> variants = new ArrayList<>();
        for (Product product : products) {
            int variantCount = random.nextInt(4) + 1; // 1 to 4 variants per product
            int totalStockForProduct = 0;
            for (int i = 0; i < variantCount; i++) {
                int stock = random.nextInt(100) + 10; // 10 to 109 stock per variant
                totalStockForProduct += stock;
                variants.add(ProductVariant.builder()
                        .size(faker.options().option("S", "M", "L", "XL", "Talla Única"))
                        .color(faker.color().name())
                        .stock(stock)
                        .sku(generateSKU(product.getName()))
                        .product(product)
                        .build());
            }
            product.setTotalStock(totalStockForProduct); // Update product's total stock
        }
        productRepo.saveAll(products); // Save products to update their stock counts
        productVariantRepo.saveAll(variants);
        System.out.println(variants.size() + " product variants created.");
    }

    private String generateSKU(String productName) {
        String prefix = productName.substring(0, Math.min(3, productName.length())).toUpperCase();
        return String.format("%s-%04d-%03d", prefix, random.nextInt(10000), random.nextInt(1000));
    }

    private void seedDiscountCodes() {
        if (discountCodeRepo.count() > 0) return;
        System.out.println("Seeding discount codes...");
        List<DiscountCode> codes = new ArrayList<>();
        for (int i = 0; i < NUM_DISCOUNT_CODES; i++) {
            codes.add(DiscountCode.builder()
                    .code(faker.commerce().promotionCode().toUpperCase())
                    .discountAmount(random.nextInt(50) + 5) // 5 to 54 percent/amount
                    .expiryDate(faker.date().future(180, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                    .isActive(random.nextBoolean())
                    .build());
        }
        discountCodeRepo.saveAll(codes);
        System.out.println(codes.size() + " discount codes created.");
    }

    private void seedOrders() {
        if (orderRepo.count() > 0) return;
        System.out.println("Seeding orders...");
        List<User> users = userRepo.findAll();
        if (users.isEmpty()) return;

        List<Order> orders = new ArrayList<>();
        Order.Status[] statuses = Order.Status.values();
        for (int i = 0; i < NUM_ORDERS; i++) {
            orders.add(Order.builder()
                    .orderDate(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .status(statuses[random.nextInt(statuses.length)])
                    .totalAmount(BigDecimal.ZERO) // Will be calculated and updated later
                    .user(users.get(random.nextInt(users.size())))
                    .build());
        }
        orderRepo.saveAll(orders);
        System.out.println(orders.size() + " orders created.");
    }

    private void seedOrderItemsAndUpdateTotals() {
        if (orderItemRepo.count() > 0) return;
        System.out.println("Seeding order items and updating order totals...");
        List<Order> orders = orderRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();
        if (orders.isEmpty() || variants.isEmpty()) return;

        List<OrderItem> orderItems = new ArrayList<>();
        for (Order order : orders) {
            int itemCount = random.nextInt(5) + 1; // 1 to 5 items per order
            BigDecimal orderTotal = BigDecimal.ZERO;
            Set<ProductVariant> usedVariants = new HashSet<>();

            for (int i = 0; i < itemCount; i++) {
                ProductVariant variant;
                // Ensure we don't add the same product variant twice to the same order
                do {
                    variant = variants.get(random.nextInt(variants.size()));
                } while (usedVariants.contains(variant));
                usedVariants.add(variant);

                int quantity = random.nextInt(3) + 1;
                BigDecimal unitPrice = variant.getProduct().getBasePrice();
                BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
                orderTotal = orderTotal.add(itemTotal);

                orderItems.add(OrderItem.builder()
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .order(order)
                        .productVariant(variant)
                        .build());
            }
            order.setTotalAmount(orderTotal.setScale(2, RoundingMode.HALF_UP));
        }
        orderItemRepo.saveAll(orderItems);
        orderRepo.saveAll(orders); // Save orders again to update total amounts
        System.out.println(orderItems.size() + " order items created and order totals updated.");
    }

    private void seedPayments() {
        if (paymentRepo.count() > 0) return;
        System.out.println("Seeding payments...");
        List<Order> orders = orderRepo.findAll();
        if (orders.isEmpty()) return;

        List<String> paymentMethods = List.of("Credit Card", "PayPal", "Apple Pay", "Google Pay", "Stripe", "Bank Transfer");
        Payment.Status[] statuses = Payment.Status.values();
        List<Payment> payments = orders.stream()
                .filter(order -> order.getPayment() == null && order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0)
                .map(order -> {
                    Payment payment = Payment.builder()
                            .paymentMethod(paymentMethods.get(random.nextInt(paymentMethods.size())))
                            .amount(order.getTotalAmount().doubleValue())
                            .status(statuses[random.nextInt(statuses.length)])
                            .build();
                    payment.setOrder(order);
                    return payment;
                }).collect(Collectors.toList());
        paymentRepo.saveAll(payments);
        System.out.println(payments.size() + " payments created.");
    }

    private void seedShippingAddresses() {
        if (shippingAddressRepo.count() > 0) return;
        System.out.println("Seeding shipping addresses...");
        List<Order> orders = orderRepo.findAll();
        if (orders.isEmpty()) return;

        List<ShippingAddress> addresses = orders.stream()
                .map(order -> ShippingAddress.builder()
                        .street(faker.address().streetAddress())
                        .city(faker.address().city())
                        .state(faker.address().state())
                        .zipCode(faker.address().zipCode())
                        .country("España")
                        .order(order)
                        .build())
                .collect(Collectors.toList());
        shippingAddressRepo.saveAll(addresses);
        System.out.println(addresses.size() + " shipping addresses created.");
    }

    private void seedCartItems() {
        if (cartItemRepo.count() > 0) return;
        System.out.println("Seeding cart items...");
        List<Cart> carts = cartRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();
        if (carts.isEmpty() || variants.isEmpty()) return;

        List<CartItem> cartItems = new ArrayList<>();
        for (Cart cart : carts) {
            // Only add items to some carts to simulate active and abandoned carts
            if (random.nextDouble() < 0.7) { // 70% of carts will have items
                int itemCount = random.nextInt(4) + 1; // 1 to 4 items
                for (int i = 0; i < itemCount; i++) {
                    cartItems.add(CartItem.builder()
                            .quantity(random.nextInt(3) + 1)
                            .cart(cart)
                            .productVariant(variants.get(random.nextInt(variants.size())))
                            .build());
                }
            }
        }
        cartItemRepo.saveAll(cartItems);
        System.out.println(cartItems.size() + " cart items created.");
    }

    private void seedProductReviews() {
        if (productReviewRepo.count() > 0) return;
        System.out.println("Seeding product reviews...");
        List<User> users = userRepo.findAll();
        List<Product> products = productRepo.findAll();
        if (users.isEmpty() || products.isEmpty()) return;

        List<ProductReview> reviews = new ArrayList<>();
        for (int i = 0; i < NUM_REVIEWS; i++) {
            reviews.add(ProductReview.builder()
                    .rating(random.nextInt(5) + 1) // 1 to 5 stars
                    .comment(faker.lorem().paragraph(2))
                    .createdAt(faker.date().past(365 * 2, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                    .user(users.get(random.nextInt(users.size())))
                    .product(products.get(random.nextInt(products.size())))
                    .build());
        }
        productReviewRepo.saveAll(reviews);
        System.out.println(reviews.size() + " product reviews created.");
    }
}

