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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A {@link CommandLineRunner} component that seeds the application database
 * with realistic, tech-focused sample data for development and testing.
 * <p>
 * This class populates all major entities (users, products, orders, etc.)
 * with consistent, semi-realistic data using {@link Faker} and predefined
 * mappings for brands and product naming conventions.
 * </p>
 * <p>
 * Seeding is idempotent: checks if data already exists before inserting.
 * All operations are transactional where needed to ensure consistency.
 * </p>
 */
@Component
public class DataSeeder implements CommandLineRunner {

    // Configuration constants
    private static final int NUM_USERS = 50;
    private static final int NUM_PRODUCTS_PER_CATEGORY = 15;
    private static final int NUM_ORDERS = 50;
    private static final int NUM_DISCOUNT_CODES = 10;
    private static final int NUM_REVIEWS = 70;

    // Maps for realistic product generation
    private static final Map<String, List<String>> CATEGORY_BRANDS = createCategoryBrandsMap();
    private static final Map<String, List<String>> PRODUCT_NAMES = createProductNamesMap();

    /**
     * Creates a mapping of tech categories to their most relevant brands.
     * Ensures product names are realistic and aligned with real-world manufacturers.
     */
    private static Map<String, List<String>> createCategoryBrandsMap() {
        Map<String, List<String>> map = new HashMap<>();

        map.put("Smartphones", Arrays.asList(
                "Apple", "Samsung", "Xiaomi", "Google", "Huawei", "OnePlus", "Sony", "Motorola", "Oppo", "Realme", "Asus", "Nokia"
        ));
        map.put("Computers", Arrays.asList(
                "Apple", "Dell", "HP", "Lenovo", "Microsoft", "Acer", "MSI", "Razer", "Asus", "Alienware"
        ));
        map.put("Tablets", Arrays.asList(
                "Apple", "Samsung", "Microsoft", "Lenovo", "Xiaomi", "Huawei"
        ));
        map.put("Smartwatches", Arrays.asList(
                "Apple", "Samsung", "Xiaomi", "Google", "Huawei", "OnePlus", "Sony", "Asus"
        ));
        map.put("Headphones", Arrays.asList(
                "Apple", "Samsung", "Sony", "Logitech", "Corsair", "SteelSeries", "HyperX", "Razer"
        ));

        List<String> peripheralBrands = Arrays.asList(
                "Logitech", "Corsair", "Razer", "SteelSeries", "HyperX", "Asus", "Dell", "HP", "Lenovo", "Microsoft"
        );
        map.put("Keyboards", peripheralBrands);
        map.put("Mice", peripheralBrands);
        map.put("Cameras", Arrays.asList("Sony", "Logitech", "Razer"));
        map.put("Audio", Arrays.asList(
                "Apple", "Google", "Sony", "LG", "Samsung", "Logitech", "Razer"
        ));
        map.put("Gaming", Arrays.asList("Sony", "Microsoft"));
        map.put("Smart Home", Arrays.asList(
                "Google", "Apple", "Samsung", "Xiaomi", "LG", "TCL"
        ));
        map.put("Accessories", Arrays.asList(
                "Apple", "Samsung", "Xiaomi", "Google", "Huawei", "OnePlus", "Sony", "LG", "Motorola", "Oppo",
                "Vivo", "Realme", "Honor", "Nothing", "Asus", "Nokia", "TCL", "Fairphone", "RedMagic", "Dell",
                "HP", "Lenovo", "Microsoft", "Acer", "MSI", "Razer", "Logitech", "Corsair", "SteelSeries", "HyperX", "Alienware"
        ));

        return map;
    }

    /**
     * Creates a mapping of category-specific product naming templates.
     * Used to generate natural-sounding product names (e.g., "iPhone 15 Pro").
     */
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

    // === Dependencies (Repositories) ===
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
    private final WishlistRepository wishlistRepo;
    private final PasswordEncoder passwordEncoder;

    private final Faker faker = new Faker(Locale.ENGLISH);
    private final Random random = new Random();

    @Autowired
    public DataSeeder(
            UserRepository userRepo,
            OrderRepository orderRepo,
            DiscountCodeRepository discountCodeRepo,
            PaymentRepository paymentRepo,
            CartRepository cartRepo,
            CartItemRepository cartItemRepo,
            ProductReviewRepository productReviewRepo,
            ProductRepository productRepo,
            CategoryRepository categoryRepo,
            OrderItemRepository orderItemRepo,
            ProductVariantRepository productVariantRepo,
            ShippingAddressRepository shippingAddressRepo,
            WishlistRepository wishlistRepo,
            PasswordEncoder passwordEncoder
    ) {
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
        this.shippingAddressRepo = shippingAddressRepo;
        this.wishlistRepo = wishlistRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Executes the data seeding process in a specific order to satisfy foreign key constraints.
     * Each method checks if data already exists to avoid duplication.
     */
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Starting tech e-commerce data seeding process...");

        if (userRepo.count() == 0) seedUsers();
        if (cartRepo.count() == 0) seedCarts();
        if (categoryRepo.count() == 0) seedCategories();
        if (productRepo.count() == 0) seedProducts();
        if (productVariantRepo.count() == 0) seedProductVariants();
        if (discountCodeRepo.count() == 0) seedDiscountCodes();
        if (orderRepo.count() == 0) seedOrders();
        if (orderItemRepo.count() == 0) seedOrderItemsAndUpdateTotals();
        if (paymentRepo.count() == 0) seedPayments();
        if (shippingAddressRepo.count() == 0) seedShippingAddresses();
        if (cartItemRepo.count() == 0) seedCartItems();
        if (productReviewRepo.count() == 0) seedProductReviews();
        if (wishlistRepo.count() == 0) seedWishlists();

        System.out.println("Tech e-commerce data seeding completed successfully!");
    }

    // === SEEDING METHODS ===

    /**
     * Seeds initial users including admin, seller, test user, and generated customers.
     * All users are verified for testing convenience.
     */
    private void seedUsers() {
        if (userRepo.count() > 0) return;

        System.out.println("Seeding tech users...");

        List<User> users = new ArrayList<>();

        // Admin user
        User admin = new User(null, "Tech Admin", "admin@techstore.com", passwordEncoder.encode("admin123"), "admin.png", User.Role.ADMIN);
        admin.setVerified(true);
        admin.addAddress(new Address(faker.name().title(), faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
        users.add(admin);

        // Seller user
        User seller = new User(null, "Tech Seller", "seller@techstore.com", passwordEncoder.encode("seller123"), "seller.png", User.Role.SELLER);
        seller.setVerified(true);
        seller.addAddress(new Address(faker.name().title(), faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
        users.add(seller);

        // Test user
        User testUser = new User(null, "test", "test@test.com", passwordEncoder.encode("123456"), "user.png", User.Role.CUSTOMER);
        testUser.setVerified(true);
        testUser.addAddress(new Address(faker.name().title(), faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
        users.add(testUser);

        // Generated users
        for (int i = 0; i < NUM_USERS - 3; i++) {
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

            int addressCount = random.nextInt(2) + 1;
            for (int j = 0; j < addressCount; j++) {
                user.addAddress(new Address(
                        faker.name().title(),
                        faker.address().streetAddress(),
                        faker.address().city(),
                        faker.address().state(),
                        faker.address().zipCode(),
                        "United States"
                ));
            }

            user.setVerified(true); // For testing
            users.add(user);
        }

        userRepo.saveAll(users);
        System.out.println(users.size() + " tech users created.");
    }

    /**
     * Seeds shopping carts for users who don't already have one.
     */
    private void seedCarts() {
        if (cartRepo.count() > 0) return;

        System.out.println("Seeding carts...");

        List<Cart> carts = userRepo.findAll().stream()
                .filter(user -> user.getCart() == null)
                .map(user -> {
                    Cart cart = Cart.builder()
                            .user(user)
                            .createdAt(LocalDateTime.now().minusDays(random.nextInt(30)))
                            .build();
                    user.setCart(cart);
                    return cart;
                })
                .collect(Collectors.toList());

        cartRepo.saveAll(carts);
        System.out.println(carts.size() + " carts created.");
    }

    /**
     * Seeds main product categories with names and UI icons.
     */
    private void seedCategories() {
        if (categoryRepo.count() > 0) return;

        System.out.println("Seeding categories...");

        List<String> categoryNames = Arrays.asList(
                "Smartphones", "Smartwatches", "Cameras", "Headphones", "Computers",
                "Keyboards", "Mice", "Gaming", "Tablets", "Smart Home", "Audio", "Accessories"
        );

        List<String> categoryIcons = Arrays.asList(
                "gi-smartphone", "bi-smartwatch", "bi-camera", "la-headphones-solid",
                "bi-laptop", "bi-keyboard", "bi-mouse", "gi-console-controller",
                "co-tablet", "ri-home-wifi-line", "hi-music-note", "md-cable"
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

    /**
     * Seeds realistic products with category-specific attributes (CPU, RAM, etc.).
     * Uses brand and naming maps to generate believable product names.
     */
    private void seedProducts() {
        if (productRepo.count() > 0) return;

        System.out.println("Seeding products with realistic names and attributes...");

        List<Category> categories = categoryRepo.findAll();
        if (categories.isEmpty()) {
            System.out.println("No categories found. Skipping product seeding.");
            return;
        }

        Set<String> allBrandsSet = new HashSet<>();
        CATEGORY_BRANDS.values().forEach(allBrandsSet::addAll);
        List<String> allBrands = new ArrayList<>(allBrandsSet);

        List<Product> products = new ArrayList<>();

        for (Category category : categories) {
            String categoryName = category.getName();
            List<String> validBrands = CATEGORY_BRANDS.getOrDefault(categoryName, allBrands);
            List<String> nameTemplates = PRODUCT_NAMES.getOrDefault(categoryName, Collections.singletonList("Product"));

            for (int i = 0; i < NUM_PRODUCTS_PER_CATEGORY; i++) {
                String brand = validBrands.get(random.nextInt(validBrands.size()));
                String nameTemplate = nameTemplates.get(random.nextInt(nameTemplates.size()));

                String productName = "Gaming".equals(categoryName) ? nameTemplate : brand + " " + nameTemplate;

                double minPrice = 50.00;
                double maxPrice = 2500.00;
                double randomPrice = minPrice + (maxPrice - minPrice) * random.nextDouble();
                BigDecimal price = BigDecimal.valueOf(randomPrice).setScale(2, RoundingMode.HALF_UP);

                Product.ProductBuilder productBuilder = Product.builder()
                        .name(productName)
                        .brand(brand)
                        .description(faker.lorem().sentence(10))
                        .createdAt(randomCreatedAt(random))
                        .rating(faker.number().randomDouble(1, 0, 5))
                        .isFeatured(random.nextBoolean() && i < 3)
                        .basePrice(price)
                        .totalStock(0)
                        .category(category);

                switch (categoryName) {
                    case "Smartphones":
                        String cpu = switch (brand) {
                            case "Apple" -> faker.options().option("A16 Bionic", "A17 Pro");
                            case "Google" -> faker.options().option("Tensor G2", "Tensor G3");
                            case "Samsung" -> faker.options().option("Exynos 2400", "Snapdragon 8 Gen 3 for Galaxy");
                            default -> faker.options().option("Snapdragon 8 Gen 3", "Dimensity 9300");
                        };
                        productBuilder
                                .cpu(cpu)
                                .memory(faker.options().option("32GB", "64GB", "128GB", "256GB", "512GB", "1TB", "2TB", "4TB", "8TB"))
                                .camera(faker.options().option("48MP", "50MP", "200MP"));
                        break;

                    case "Computers":
                        String processor, gpu, os;
                        if ("Apple".equals(brand)) {
                            processor = faker.options().option("M2 Pro", "M3", "M3 Pro", "M3 Max");
                            gpu = "Apple Integrated Graphics";
                            os = "macOS";
                        } else {
                            processor = faker.options().option("Intel Core Ultra 7", "Intel Core Ultra 9", "AMD Ryzen 7", "AMD Ryzen 9");
                            gpu = faker.options().option("NVIDIA RTX 4070", "AMD Radeon RX 7800M", "Intel Arc Graphics");
                            os = faker.options().option("Windows 11", "Linux");
                        }
                        productBuilder
                                .processorModel(processor)
                                .graphicsCard(gpu)
                                .operatingSystem(os)
                                .ramCapacity(new Integer[]{16, 32, 64}[random.nextInt(3)])
                                .storageCapacity(new Integer[]{512, 1024, 2048}[random.nextInt(3)]);
                        break;

                    case "Tablets":
                        String tabletOs = "Apple".equals(brand) ? "iPadOS" :
                                "Microsoft".equals(brand) ? "Windows 11" : "Android";
                        productBuilder
                                .operatingSystem(tabletOs)
                                .screenSize(faker.options().option("10.2\"", "11\"", "12.9\""))
                                .storageCapacity(new Integer[]{128, 256, 512}[random.nextInt(3)])
                                .ramCapacity(new Integer[]{8, 12, 16}[random.nextInt(3)]);
                        break;

                    case "Smartwatches":
                        productBuilder.compatibility("Apple".equals(brand) ? "iOS" : "Android");
                        break;

                    case "Gaming":
                        productBuilder.platform(faker.options().option("PlayStation 5", "Xbox Series X", "PC", "Nintendo Switch"));
                        break;

                    case "Accessories":
                        productBuilder
                                .material(faker.commerce().material())
                                .color(faker.color().name());
                        break;
                }

                products.add(productBuilder.build());
            }
        }

        productRepo.saveAll(products);
        System.out.println(products.size() + " realistic products created.");
    }

    /**
     * Generates a random creation timestamp within the last 30 days.
     */
    private LocalDateTime randomCreatedAt(Random random) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = LocalDate.now().toEpochDay();
        long randomDay = ThreadLocalRandom.current().longs(startEpochDay, endEpochDay + 1).findAny().orElse(startEpochDay);
        return LocalDateTime.of(LocalDate.ofEpochDay(randomDay), LocalTime.MIN)
                .plusMinutes(random.nextInt(1440));
    }

    /**
     * Seeds product variants (color, size, stock) for each product.
     * Updates the parent product's total stock.
     */
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
            int variantCount = random.nextInt(4) + 1;
            int totalStockForProduct = 0;

            for (int i = 0; i < variantCount; i++) {
                int stock = random.nextInt(100) + 10;
                totalStockForProduct += stock;

                variants.add(ProductVariant.builder()
                        .size(faker.options().option("S", "M", "L", "XL", "Talla Única"))
                        .color(faker.color().name())
                        .stock(stock)
                        .sku(generateSKU(product.getName()))
                        .product(product)
                        .build());
            }

            product.setTotalStock(totalStockForProduct);
        }

        productRepo.saveAll(products);
        productVariantRepo.saveAll(variants);
        System.out.println(variants.size() + " product variants created.");
    }

    /**
     * Generates a SKU based on the product name.
     */
    private String generateSKU(String productName) {
        String prefix = productName.substring(0, Math.min(3, productName.length())).toUpperCase();
        return String.format("%s-%04d-%03d", prefix, random.nextInt(10000), random.nextInt(1000));
    }

    /**
     * Seeds discount codes with random expiry and activation status.
     */
    private void seedDiscountCodes() {
        if (discountCodeRepo.count() > 0) return;

        System.out.println("Seeding discount codes...");

        List<DiscountCode> codes = IntStream.range(0, NUM_DISCOUNT_CODES)
                .mapToObj(i -> DiscountCode.builder()
                        .code(faker.commerce().promotionCode().toUpperCase())
                        .discountAmount(random.nextInt(50) + 5)
                        .expiryDate(faker.date().future(180, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                        .isActive(random.nextBoolean())
                        .build())
                .collect(Collectors.toList());

        discountCodeRepo.saveAll(codes);
        System.out.println(codes.size() + " discount codes created.");
    }

    /**
     * Seeds orders with random status and dates within the past year.
     */
    private void seedOrders() {
        if (orderRepo.count() > 0) return;

        System.out.println("Seeding orders...");

        List<User> users = userRepo.findAll();
        if (users.isEmpty()) return;

        List<Order> orders = IntStream.range(0, NUM_ORDERS)
                .mapToObj(i -> Order.builder()
                        .orderDate(faker.date().past(365, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .status(Order.Status.values()[random.nextInt(Order.Status.values().length)])
                        .totalAmount(BigDecimal.ZERO)
                        .user(users.get(random.nextInt(users.size())))
                        .build())
                .collect(Collectors.toList());

        orderRepo.saveAll(orders);
        System.out.println(orders.size() + " orders created.");
    }

    /**
     * Seeds order items and calculates the total amount for each order.
     */
    private void seedOrderItemsAndUpdateTotals() {
        if (orderItemRepo.count() > 0) return;

        System.out.println("Seeding order items and updating order totals...");

        List<Order> orders = orderRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();
        if (orders.isEmpty() || variants.isEmpty()) return;

        List<OrderItem> orderItems = new ArrayList<>();

        for (Order order : orders) {
            int itemCount = random.nextInt(5) + 1;
            BigDecimal orderTotal = BigDecimal.ZERO;
            Set<ProductVariant> usedVariants = new HashSet<>();

            for (int i = 0; i < itemCount; i++) {
                ProductVariant variant;
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
        orderRepo.saveAll(orders);
        System.out.println(orderItems.size() + " order items created and order totals updated.");
    }

    /**
     * Seeds payment records for paid orders.
     */
    private void seedPayments() {
        if (paymentRepo.count() > 0) return;

        System.out.println("Seeding payments...");

        List<Order> orders = orderRepo.findAll();
        if (orders.isEmpty()) return;

        List<String> paymentMethods = List.of("Credit Card", "PayPal", "Apple Pay", "Google Pay", "Stripe");
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
                })
                .collect(Collectors.toList());

        paymentRepo.saveAll(payments);
        System.out.println(payments.size() + " payments created.");
    }

    /**
     * Seeds shipping addresses for all users.
     */
    private void seedShippingAddresses() {
        if (shippingAddressRepo.count() > 0) return;

        System.out.println("Seeding shipping addresses...");

        List<User> users = userRepo.findAll();
        if (users.isEmpty()) return;

        List<ShippingAddress> addresses = new ArrayList<>();
        for (User user : users) {
            int addressCount = random.nextInt(3) + 1;
            for (int i = 0; i < addressCount; i++) {
                addresses.add(ShippingAddress.builder()
                        .title(generateRandomTitle())
                        .addressType(getRandomAddressType())
                        .street(faker.address().streetAddress())
                        .city(faker.address().city())
                        .state(faker.address().state())
                        .zipCode(faker.address().zipCode())
                        .country("United States")
                        .user(user)
                        .build());
            }
        }

        shippingAddressRepo.saveAll(addresses);
        System.out.println(addresses.size() + " shipping addresses created for users.");
    }

    /**
     * Generates a random shipping address title.
     */
    private String generateRandomTitle() {
        String[] places = {"City", "Downtown", "Uptown", "Riverside", "Mountain", "Beach", "Park", "Center"};
        String[] types = {"Flat", "Apartment", "House", "Office", "Studio", "Loft", "Building", "Complex"};
        return places[faker.number().numberBetween(0, places.length)] + " " +
                types[faker.number().numberBetween(0, types.length)];
    }

    /**
     * Selects a random address type.
     */
    private ShippingAddress.AddressType getRandomAddressType() {
        ShippingAddress.AddressType[] types = ShippingAddress.AddressType.values();
        return types[faker.number().numberBetween(0, types.length)];
    }

    /**
     * Seeds cart items for existing carts.
     */
    private void seedCartItems() {
        if (cartItemRepo.count() > 0) return;

        System.out.println("Seeding cart items...");

        List<Cart> carts = cartRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();
        if (carts.isEmpty() || variants.isEmpty()) return;

        List<CartItem> cartItems = new ArrayList<>();
        for (Cart cart : carts) {
            if (random.nextDouble() < 0.7) { // 70% of carts have items
                int itemCount = random.nextInt(4) + 1;
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

    /**
     * Seeds product reviews with random ratings and comments.
     */
    private void seedProductReviews() {
        if (productReviewRepo.count() > 0) return;

        System.out.println("Seeding product reviews...");

        List<User> users = userRepo.findAll();
        List<Product> products = productRepo.findAll();
        if (users.isEmpty() || products.isEmpty()) return;

        List<ProductReview> reviews = IntStream.range(0, NUM_REVIEWS)
                .mapToObj(i -> ProductReview.builder()
                        .rating(random.nextInt(5) + 1)
                        .comment(faker.lorem().paragraph(2))
                        .createdAt(faker.date().past(730, TimeUnit.DAYS).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())
                        .user(users.get(random.nextInt(users.size())))
                        .product(products.get(random.nextInt(products.size())))
                        .build())
                .collect(Collectors.toList());

        productReviewRepo.saveAll(reviews);
        System.out.println(reviews.size() + " product reviews created.");
    }

    /**
     * Seeds user wishlists with random products.
     */
    private void seedWishlists() {
        if (wishlistRepo.count() > 0) return;

        System.out.println("Seeding wishlists...");

        List<User> users = userRepo.findAll();
        List<Product> products = productRepo.findAll();
        if (users.isEmpty() || products.isEmpty()) return;

        List<Wishlist> wishlists = new ArrayList<>();
        for (User user : users) {
            int numWishlists = random.nextInt(2) + 1;
            for (int i = 0; i < numWishlists; i++) {
                Wishlist wishlist = Wishlist.builder()
                        .title("Wishlist " + (i + 1) + " of " + user.getUsername())
                        .description("Auto-generated wishlist for " + user.getUsername())
                        .imageUrl("https://via.placeholder.com/150")
                        .productUrl("https://example.com/product")
                        .price("Varies")
                        .category("Mixed")
                        .user(user)
                        .products(new HashSet<>(getRandomSubset(products, random.nextInt(5) + 1)))
                        .build();
                wishlists.add(wishlist);
            }
        }

        wishlistRepo.saveAll(wishlists);
        System.out.println(wishlists.size() + " wishlists created.");
    }

    /**
     * Returns a random subset of products.
     */
    private List<Product> getRandomSubset(List<Product> list, int size) {
        size = Math.min(size, list.size());
        List<Product> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, size);
    }
}