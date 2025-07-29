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
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
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
    private static final Map<String, List<String>> BRAND_NAMING_RULES = createBrandNamingRules();

    /**
     * Creates a mapping of tech categories to their most relevant brands.
     * Ensures product names are realistic and aligned with real-world manufacturers.
     */
    private static Map<String, List<String>> createCategoryBrandsMap() {
        Map<String, List<String>> map = new HashMap<>();

        // === MOBILE & COMPUTE ===
        map.put("Smartphones", Arrays.asList(
                "Apple", "Samsung", "Google", "Xiaomi", "OnePlus", "Sony", "Motorola", "Oppo", "Realme", "Asus", "Nokia"
        ));

        map.put("Tablets", Arrays.asList(
                "Apple", "Samsung", "Microsoft", "Lenovo", "Xiaomi", "Huawei"
        ));

        map.put("Laptops", Arrays.asList(
                "Apple", "Dell", "HP", "Lenovo", "Microsoft", "Acer", "MSI", "Razer", "Asus", "Alienware", "LG"
        ));

        map.put("Handhelds", Arrays.asList(
                "ASUS", "AYANEO", "GPD", "Logitech", "Razer", "Steam", "Lenovo"
        ));

        // === INPUT & CONTROL ===
        map.put("Keyboards", Arrays.asList(
                "Logitech", "Corsair", "Razer", "SteelSeries", "HyperX", "Ducky", "Keychron", "Epomaker", "Kinesis", "Microsoft", "Apple", "Das Keyboard"
        ));

        map.put("Mice", Arrays.asList(
                "Logitech", "Razer", "Corsair", "SteelSeries", "Microsoft", "Apple", "Finalmouse", "Glorious", "Zowie"
        ));

        map.put("Controllers", Arrays.asList(
                "Sony", "Microsoft", "Nintendo", "8BitDo", "Razer", "Logitech", "Astro", "DualShock", "Scuf", "PowerA"
        ));

        return map;
    }

    /**
     * Creates a mapping of brands to realistic naming templates.
     * Prevents illogical names like "Apple Samsung".
     */
    private static Map<String, List<String>> createBrandNamingRules() {
        Map<String, List<String>> rules = new HashMap<>();

        // === MOBILE & COMPUTE ===
        rules.put("Apple", Arrays.asList(
                "iPhone %s", "iPhone %s Pro", "iPhone %s Pro Max",
                "MacBook %s", "MacBook %s Air", "MacBook %s Pro",
                "iPad %s", "iPad %s Pro", "iPad %s Air", "iPad %s Mini"
        ));

        rules.put("Samsung", Arrays.asList(
                "Galaxy S%s", "Galaxy S%s Ultra", "Galaxy S%s+", "Galaxy Z %s",
                "Galaxy Note %s", "Galaxy Tab S%s"
        ));

        rules.put("Google", Arrays.asList(
                "Pixel %s", "Pixel %s Pro", "Pixel Tablet %s"
        ));

        rules.put("Xiaomi", Arrays.asList(
                "Mi %s", "Redmi %s", "Redmi Note %s", "Poco %s", "Xiaomi %s"
        ));

        rules.put("OnePlus", Arrays.asList(
                "OnePlus %s", "OnePlus %s Pro", "OnePlus Nord %s", "OnePlus %sT"
        ));

        rules.put("Microsoft", Arrays.asList(
                "Surface %s", "Surface Pro %s", "Surface Laptop %s", "Surface Book %s",
                "Xbox Wireless Controller %s", "Elite %s", "Adaptive Controller",
                "Sculpt %s", "Ergonomic Keyboard %s", "Surface Mouse %s", "Arc Mouse %s"
        ));

        rules.put("Dell", Arrays.asList(
                "XPS %s", "Inspiron %s", "Latitude %s", "Alienware %s", "G Series %s"
        ));

        rules.put("HP", Arrays.asList(
                "Spectre %s", "Envy %s", "Pavilion %s", "Omen %s", "EliteBook %s"
        ));

        rules.put("Lenovo", Arrays.asList(
                "ThinkPad %s", "Legion %s", "IdeaPad %s", "Yoga %s", "Flex %s", "Legion Go %s"
        ));

        rules.put("ASUS", Arrays.asList(
                "ROG Zephyrus %s", "ROG Flow %s", "TUF Gaming %s", "Vivobook %s", "Zenbook %s", "ROG Ally %s"
        ));

        rules.put("Acer", Arrays.asList(
                "Predator %s", "Nitro %s", "Swift %s", "Aspire %s"
        ));

        rules.put("MSI", Arrays.asList(
                "GS %s", "GP %s", "Stealth %s", "Alpha %s"
        ));

        rules.put("Razer", Arrays.asList(
                "Blade %s", "Blade Stealth %s", "Blade Pro %s",
                "DeathAdder %s", "Viper %s", "Basilisk %s", "Naga %s",
                "BlackWidow %s", "Huntsman %s", "Ornata %s",
                "Wolverine %s", "Kishi %s"
        ));

        rules.put("Alienware", Arrays.asList(
                "m15 R%s", "m16 R%s", "x14 R%s", "x16 R%s"
        ));

        rules.put("LG", Arrays.asList(
                "Gram %s", "UltraFine %s", "UltraGear %s"
        ));

        // === HANDHELDS ===
        rules.put("AYANEO", Arrays.asList(
                "AYANEO %s", "AYANEO %s Pro", "AYANEO Geek %s", "AYANEO Slide %s"
        ));

        rules.put("GPD", Arrays.asList(
                "GPD Win %s", "GPD Pocket %s", "GPD MicroPC %s"
        ));

        rules.put("Steam", Arrays.asList(
                "Steam Deck %s", "Steam Deck OLED %s"
        ));

        // === INPUT & CONTROL ===
        rules.put("Logitech", Arrays.asList(
                "MX %s", "G %s", "Z %s", "PRO %s", "Craft %s", "POP %s", "G Cloud %s"
        ));

        rules.put("Corsair", Arrays.asList(
                "K%s", "K95 %s", "Stratix %s",
                "M%s", "Dark Core %s", "Sabre %s"
        ));

        rules.put("SteelSeries", Arrays.asList(
                "Apex %s", "Apex Pro %s", "Apex 7 %s",
                "Rival %s", "Sensei %s", "Aerox %s"
        ));

        rules.put("HyperX", Arrays.asList(
                "Alloy %s", "Pulsefire %s", "Cloud %s"
        ));

        rules.put("Keychron", Arrays.asList(
                "K%s", "Q%s", "C%s", "V%s"
        ));

        rules.put("Ducky", Arrays.asList(
                "Shine %s", "One %s", "King %s"
        ));

        rules.put("Kinesis", Arrays.asList(
                "Freestyle %s", "Advantage %s", "Ergo %s"
        ));

        rules.put("Epomaker", Arrays.asList(
                "TH80 %s", "ERGO42 %s", "AJ60 %s"
        ));

        rules.put("8BitDo", Arrays.asList(
                "Ultimate %s", "Pro %s", "Zero %s", "SN30 %s"
        ));

        rules.put("Sony", Arrays.asList(
                "Xperia %s", "Xperia %s Pro",
                "DualShock %s", "DualSense %s"
        ));

        // Fallback for any brand not explicitly listed
        rules.put("DEFAULT", Arrays.asList(
                "%s %s",
                "%s %s Edition",
                "%s Model %s",
                "%s Series %s"
        ));

        return rules;
    }

    /**
     * Checks if a brand-category combination is realistic.
     */
    private static boolean isValidBrandCategory(String brand, String category) {
        Map<String, List<String>> validCategories = new HashMap<>();

        validCategories.put("Apple", Arrays.asList("Smartphones", "Tablets", "Laptops", "Keyboards", "Mice"));
        validCategories.put("Samsung", Arrays.asList("Smartphones", "Tablets"));
        validCategories.put("Google", Arrays.asList("Smartphones", "Tablets"));
        validCategories.put("Sony", Arrays.asList("Smartphones", "Laptops", "Controllers"));
        validCategories.put("Microsoft", Arrays.asList("Laptops", "Tablets", "Keyboards", "Mice", "Controllers"));
        validCategories.put("Xiaomi", Arrays.asList("Smartphones", "Tablets"));
        validCategories.put("ASUS", Arrays.asList("Laptops", "Handhelds"));
        validCategories.put("Razer", Arrays.asList("Laptops", "Handhelds", "Keyboards", "Mice", "Controllers"));
        validCategories.put("Logitech", Arrays.asList("Handhelds", "Keyboards", "Mice", "Controllers"));
        validCategories.put("Corsair", Arrays.asList("Keyboards", "Mice"));
        validCategories.put("SteelSeries", Arrays.asList("Keyboards", "Mice"));
        validCategories.put("HyperX", Arrays.asList("Keyboards", "Mice"));
        validCategories.put("Ducky", List.of("Keyboards"));
        validCategories.put("Keychron", List.of("Keyboards"));
        validCategories.put("Epomaker", List.of("Keyboards"));
        validCategories.put("8BitDo", List.of("Controllers"));
        validCategories.put("AYANEO", List.of("Handhelds"));
        validCategories.put("GPD", List.of("Handhelds"));
        validCategories.put("Steam", List.of("Handhelds"));

        List<String> allowed = validCategories.getOrDefault(brand, null);
        if (allowed == null) return true;
        return allowed.contains(category);
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
    public void run(String... args) {
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
    private void seedUsers() {
        if (userRepo.count() > 0) return;
        System.out.println("Seeding tech users...");
        List<User> users = new ArrayList<>();
        User admin = new User(null, "Tech Admin", "admin@techstore.com", passwordEncoder.encode("admin123"), "admin.png", User.Role.ADMIN);
        admin.setVerified(true);
        admin.addAddress(new Address(faker.name().title(), faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
        users.add(admin);

        User seller = new User(null, "Tech Seller", "seller@techstore.com", passwordEncoder.encode("seller123"), "seller.png", User.Role.SELLER);
        seller.setVerified(true);
        seller.addAddress(new Address(faker.name().title(), faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
        users.add(seller);

        User testUser = new User(null, "test", "test@test.com", passwordEncoder.encode("123456"), "user.png", User.Role.CUSTOMER);
        testUser.setVerified(true);
        testUser.addAddress(new Address(faker.name().title(), faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
        users.add(testUser);

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
            user.setVerified(true);
            users.add(user);
        }
        userRepo.saveAll(users);
        System.out.println(users.size() + " tech users created.");
    }

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

    private void seedCategories() {
        if (categoryRepo.count() > 0) return;
        System.out.println("Seeding updated categories for Mobile & Compute and Input & Control...");

        List<String> categoryNames = Arrays.asList(
                // === MOBILE & COMPUTE ===
                "Smartphones",
                "Tablets",
                "Laptops",
                "Handhelds",          // e.g., gaming handhelds like Steam Deck

                // === INPUT & CONTROL ===
                "Keyboards",
                "Mice",
                "Controllers"        // e.g., gamepads, joysticks
        );

        List<String> categoryIcons = Arrays.asList(
                // MOBILE & COMPUTE
                "gi-smartphone",      // Smartphones
                "co-tablet",          // Tablets
                "bi-laptop",          // Laptops
                "bi-nintendo-switch",      // Handhelds

                // INPUT & CONTROL
                "bi-keyboard",        // Keyboards
                "bi-mouse",           // Mice
                "gi-console-controller" // Controllers
        );

        List<Category> categories = IntStream.range(0, categoryNames.size())
                .mapToObj(i -> Category.builder()
                        .name(categoryNames.get(i))
                        .icon(categoryIcons.get(i))
                        .build())
                .collect(Collectors.toList());

        categoryRepo.saveAll(categories);
        System.out.println(categories.size() + " updated categories created.");
    }

    /**
     * Seeds realistic products with brand-aligned naming (e.g., iPhone 15 Pro, Galaxy S24).
     * Now fully compatible with the new Product entity structure.
     * No legacy fields are used.
     */
    private void seedProducts() {
        if (productRepo.count() > 0) return;
        System.out.println("Seeding products with realistic names and modern attributes...");
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
            // Filter brands that are valid for this category
            List<String> filteredBrands = validBrands.stream()
                    .filter(brand -> isValidBrandCategory(brand, categoryName))
                    .collect(Collectors.toList());
            if (filteredBrands.isEmpty()) filteredBrands = validBrands;

            for (int i = 0; i < NUM_PRODUCTS_PER_CATEGORY; i++) {
                String brand = filteredBrands.get(random.nextInt(filteredBrands.size()));
                // Use brand-specific naming rules
                List<String> namingTemplates = BRAND_NAMING_RULES.getOrDefault(brand, BRAND_NAMING_RULES.get("DEFAULT"));
                String template = namingTemplates.get(random.nextInt(namingTemplates.size()));
                String productName = injectRealisticValues(template, brand, categoryName);

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

                // === MOBILE & COMPUTE CATEGORIES ===
                if (List.of("Smartphones", "Tablets", "Computers").contains(categoryName)) {
                    String os = "";
                    String cpu = "";
                    Integer ram = null;
                    String storage = "";
                    String gpu = "Integrated Graphics";
                    Integer refreshRate = 60;

                    switch (categoryName) {
                        case "Smartphones":
                            os = "Apple".equals(brand) ? "iOS" : "Android";
                            if ("Apple".equals(brand)) {
                                cpu = faker.options().option("A16 Bionic", "A17 Pro");
                            } else if ("Google".equals(brand)) {
                                cpu = "Google Tensor G3";
                            } else if ("Samsung".equals(brand)) {
                                cpu = faker.options().option("Exynos 2400", "Snapdragon 8 Gen 3 for Galaxy");
                            } else {
                                cpu = faker.options().option("Snapdragon 8 Gen 3", "Dimensity 9300");
                            }
                            Integer[] ramOptions = {8, 12, 16};
                            ram = ramOptions[random.nextInt(ramOptions.length)];
                            storage = faker.options().option("128GB", "256GB", "512GB", "1TB");
                            gpu = faker.options().option("Adreno 750", "Mali-G72", "Apple GPU 6-Core");
                            Integer[] refreshRateOptions = {60, 90, 120};
                            refreshRate = refreshRateOptions[random.nextInt(refreshRateOptions.length)];
                            break;

                        case "Tablets":
                            os = "Apple".equals(brand) ? "iPadOS" :
                                    "Microsoft".equals(brand) ? "Windows 11" : "Android";
                            cpu = faker.options().option("Apple M2", "Snapdragon 8 Gen 3", "MediaTek Kompanio");
                            Integer[] tabletRamOptions = {8, 12, 16};
                            ram = tabletRamOptions[random.nextInt(tabletRamOptions.length)];
                            storage = faker.options().option("64GB eMMC", "128GB SSD", "256GB NVMe", "512GB UFS");
                            gpu = faker.options().option("Apple M2 GPU", "Adreno 780", "Mali-G715");
                            Integer[] tabletRefreshRateOptions = {60, 90, 120};
                            refreshRate = tabletRefreshRateOptions[random.nextInt(tabletRefreshRateOptions.length)];
                            break;

                        case "Computers":
                            os = "Apple".equals(brand) ? "macOS" : faker.options().option("Windows 11", "Linux");
                            if ("Apple".equals(brand)) {
                                cpu = faker.options().option("M2 Pro", "M3", "M3 Pro", "M3 Max");
                                gpu = "Apple Integrated Graphics";
                            } else {
                                cpu = faker.options().option("Intel Core Ultra 7", "AMD Ryzen 7", "Intel Core i7");
                                gpu = faker.options().option("NVIDIA RTX 4070", "AMD Radeon RX 7800M", "Intel Arc Graphics");
                            }
                            Integer[] computerRamOptions = {16, 32, 64};
                            ram = computerRamOptions[random.nextInt(computerRamOptions.length)];
                            storage = faker.options().option("512GB SSD", "1TB NVMe", "2TB SSD");
                            refreshRate = 60; // desktops usually 60Hz
                            break;
                    }

                    // Set Mobile & Compute fields
                    productBuilder
                            .cpu(cpu)
                            .ram(ram)
                            .storage(storage)
                            .gpu(gpu)
                            .os(os)
                            .screenSize(faker.options().option("6.1\"", "6.7\"", "10.5\"", "13.3\"", "15.6\"", "17.3\""))
                            .refreshRate(refreshRate)
                            .camera(faker.options().option("12MP", "48MP", "50MP", "200MP"))
                            .frontCamera(faker.options().option("12MP", "16MP", "32MP"))
                            .battery(String.valueOf(faker.number().numberBetween(3000, 6000)) + "mAh");
                }

                // === INPUT & CONTROL CATEGORIES ===
                else if (List.of("Keyboards", "Mice").contains(categoryName)) {
                    if ("Keyboards".equals(categoryName)) {
                        String switchType = faker.options().option("Mechanical - Red Cherry MX", "Mechanical - Blue Kailh", "Optical", "Scissor");
                        String backlighting = faker.options().option("RGB", "White LED", "Single-color", "None");

                        Integer[] keyboardDpiOptions = {800, 1600, 3200};
                        Integer[] keyboardPollingRateOptions = {125, 500, 1000};

                        productBuilder
                                .switchType(switchType)
                                .backlighting(backlighting)
                                .programmableButtons(faker.bool().bool())
                                .dpi(keyboardDpiOptions[random.nextInt(keyboardDpiOptions.length)])
                                .pollingRate(keyboardPollingRateOptions[random.nextInt(keyboardPollingRateOptions.length)])
                                .batteryLife(faker.options().option("40 hours", "100 hours", "200 hours"));
                    }

                    if ("Mice".equals(categoryName)) {
                        Integer[] mouseDpiOptions = {800, 1600, 2400, 3200, 5000};
                        Integer[] mousePollingRateOptions = {125, 250, 500, 1000};

                        productBuilder
                                .dpi(mouseDpiOptions[random.nextInt(mouseDpiOptions.length)])
                                .pollingRate(mousePollingRateOptions[random.nextInt(mousePollingRateOptions.length)])
                                .programmableButtons(faker.bool().bool())
                                .batteryLife(faker.options().option("30 days", "60 days", "100 hours"));
                    }
                }

                // === OTHER CATEGORIES (no special attributes) ===
                else {
                    // No special attributes for other categories in the current Product entity
                }

                products.add(productBuilder.build());
            }
        }
        productRepo.saveAll(products);
        System.out.println(products.size() + " realistic products created with updated schema.");
    }

    /**
     * Injects realistic values into naming templates based on BOTH brand and category.
     * This ensures products get appropriate names for their category.
     */
    private String injectRealisticValues(String template, String brand, String category) {
        // Use a copy to avoid modifying the original template in the map
        String result = template;

        // Category-specific naming logic
        // Use a loop that replaces one placeholder at a time for templates with multiple "%s"
        while (result.contains("%s")) {
            String replacement = "";

            switch (category) {
                case "Smartphones":
                    if ("Apple".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(13, 16)); // iPhone 13-16
                    } else if ("Samsung".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(21, 25)); // Galaxy S21-S25
                    } else if ("Google".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(7, 9)); // Pixel 7-9
                    } else if ("Sony".equals(brand)) {
                        replacement = faker.options().option("1 V", "5 V", "10 V"); // Xperia models
                    } else if ("OnePlus".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(10, 12));
                    } else if ("Xiaomi".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(12, 14));
                    } else {
                        replacement = String.valueOf(faker.number().numberBetween(9, 15));
                    }
                    break;

                case "Tablets":
                    if ("Apple".equals(brand)) {
                        replacement = faker.options().option("Air", "Pro 11", "Pro 12.9", "Mini", "10th Gen");
                    } else if ("Samsung".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(8, 9)); // Tab S8-S9
                    } else {
                        replacement = String.valueOf(faker.number().numberBetween(8, 12));
                    }
                    break;

                case "Laptops":
                    if ("Apple".equals(brand)) {
                        replacement = faker.options().option("Air M2", "Air M3", "Pro M2", "Pro M3", "14-inch", "16-inch");
                    } else if ("Dell".equals(brand) || "HP".equals(brand) || "Lenovo".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(13, 17)); // Screen sizes
                    } else if ("Razer".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(14, 18));
                    } else if ("Alienware".equals(brand)) {
                        replacement = String.valueOf(faker.number().numberBetween(5, 8)); // e.g., R5, R6
                    } else {
                        replacement = faker.options().option("13", "14", "15", "16", "17");
                    }
                    break;

                case "Controllers":
                    if ("Sony".equals(brand)) {
                        replacement = faker.options().option("Edge", "Wireless", "Portal");
                    } else if ("Microsoft".equals(brand)) {
                        replacement = faker.options().option("Series X/S", "Elite Series 2", "Core", "Wireless");
                    } else if ("8BitDo".equals(brand)) {
                        replacement = faker.options().option("Pro 2", "Ultimate", "Zero 2", "SN30 Pro");
                    } else if ("Razer".equals(brand)) {
                        replacement = faker.options().option("Wolverine V2", "Kishi V2", "Raiju");
                    } else {
                        replacement = faker.options().option("Pro", "Elite", "Wireless", "Ultimate");
                    }
                    break;

                case "Keyboards":
                    if ("Logitech".equals(brand)) {
                        replacement = faker.options().option("MX Keys S", "G915 TKL", "G Pro X TKL", "Wave Keys");
                    } else if ("Razer".equals(brand)) {
                        replacement = faker.options().option("BlackWidow V4", "Huntsman V3", "Ornata V3");
                    } else if ("Corsair".equals(brand)) {
                        replacement = faker.options().option("K70", "K100", "K65");
                    } else if (Arrays.asList("Keychron", "Ducky").contains(brand)) {
                        replacement = faker.options().option("K2", "Q1", "V1", "One 3");
                    } else {
                        replacement = faker.options().option("Pro", "Elite", "Gaming", "Mechanical", "TKL");
                    }
                    break;

                case "Mice":
                    if ("Logitech".equals(brand)) {
                        replacement = faker.options().option("MX Master 3S", "G Pro X Superlight 2", "G502 X");
                    } else if ("Razer".equals(brand)) {
                        replacement = faker.options().option("DeathAdder V3 Pro", "Viper V2 Pro", "Basilisk V3 Pro");
                    } else if ("SteelSeries".equals(brand)) {
                        replacement = faker.options().option("Aerox 5", "Rival 3", "Sensei Ten");
                    } else {
                        replacement = faker.options().option("Pro", "Gaming", "Wireless", "Elite");
                    }
                    break;

                case "Handhelds":
                    if ("Steam".equals(brand)) {
                        replacement = faker.options().option("256GB", "512GB", "1TB");
                    } else if ("ASUS".equals(brand)) {
                        replacement = faker.options().option("Z1 Extreme", "Z1");
                    } else if ("Lenovo".equals(brand)) {
                        replacement = faker.options().option("512GB", "1TB");
                    } else {
                        replacement = faker.options().option("Pro", "OLED", "Ultimate", "Win 4");
                    }
                    break;

                default:
                    // Fallback for any other category
                    replacement = faker.options().option("Pro", "Max", "Ultra", "Elite", "Plus", "Standard Edition");
                    break;
            }

            // Replace only the first occurrence of "%s" to handle multiple placeholders correctly
            result = result.replaceFirst("%s", Matcher.quoteReplacement(replacement));
        }

        return result.trim();
    }

    private LocalDateTime randomCreatedAt(Random random) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = LocalDate.now().toEpochDay();
        long randomDay = ThreadLocalRandom.current().longs(startEpochDay, endEpochDay + 1).findAny().orElse(startEpochDay);
        return LocalDateTime.of(LocalDate.ofEpochDay(randomDay), LocalTime.MIN)
                .plusMinutes(random.nextInt(1440));
    }

    /**
     * Seeds product variants with realistic options based on product category.
     * Creates category-appropriate variants:
     * - Smartphones/Tablets: Storage and color options
     * - Laptops: RAM/Storage configurations and colors
     * - Keyboards/Mice: Switch types, colors, and connectivity
     * - Controllers: Colors and special editions
     * - Handhelds: Storage options and colors
     */
    private void seedProductVariants() {
        if (productVariantRepo.count() > 0) return;
        System.out.println("Seeding realistic product variants based on categories...");
        List<Product> products = productRepo.findAll();
        if (products.isEmpty()) {
            System.out.println("No products found. Skipping variant seeding.");
            return;
        }

        List<ProductVariant> variants = new ArrayList<>();
        for (Product product : products) {
            String categoryName = product.getCategory().getName();
            List<ProductVariant> productVariants = createVariantsForCategory(product, categoryName);
            variants.addAll(productVariants);

            // Calculate total stock for the product
            int totalStockForProduct = productVariants.stream()
                    .mapToInt(ProductVariant::getStock)
                    .sum();
            product.setTotalStock(totalStockForProduct);
        }

        productRepo.saveAll(products);
        productVariantRepo.saveAll(variants);
        System.out.println(variants.size() + " realistic product variants created.");
    }

    /**
     * Creates appropriate variants for a product based on its category.
     */
    private List<ProductVariant> createVariantsForCategory(Product product, String categoryName) {
        List<ProductVariant> variants = new ArrayList<>();
        String brand = product.getBrand();

        switch (categoryName) {
            case "Smartphones":
                variants.addAll(createSmartphoneVariants(product, brand));
                break;
            case "Tablets":
                variants.addAll(createTabletVariants(product, brand));
                break;
            case "Laptops":
                variants.addAll(createLaptopVariants(product, brand));
                break;
            case "Handhelds":
                variants.addAll(createHandheldVariants(product, brand));
                break;
            case "Keyboards":
                variants.addAll(createKeyboardVariants(product, brand));
                break;
            case "Mice":
                variants.addAll(createMouseVariants(product, brand));
                break;
            case "Controllers":
                variants.addAll(createControllerVariants(product, brand));
                break;
            default:
                // Fallback for any other categories
                variants.addAll(createGenericVariants(product));
                break;
        }

        return variants;
    }

    private List<ProductVariant> createSmartphoneVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Storage options based on brand
        List<String> storageOptions;
        if ("Apple".equals(brand)) {
            storageOptions = Arrays.asList("128GB", "256GB", "512GB", "1TB");
        } else {
            storageOptions = Arrays.asList("128GB", "256GB", "512GB");
        }

        // Restricted smartphone colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        // Create variants for each storage-color combination
        for (String storage : storageOptions) {
            // Not all colors available for all storage options (realistic)
            int colorCount = Math.min(random.nextInt(4) + 2, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(50) + 5; // Lower stock for phones
                variants.add(ProductVariant.builder()
                        .size(storage) // Using size field for storage
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + storage + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createTabletVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Storage options for tablets
        List<String> storageOptions;
        if ("Apple".equals(brand)) {
            storageOptions = Arrays.asList("64GB", "256GB", "512GB", "1TB", "2TB");
        } else if ("Microsoft".equals(brand)) {
            storageOptions = Arrays.asList("128GB", "256GB", "512GB", "1TB");
        } else {
            storageOptions = Arrays.asList("64GB", "128GB", "256GB", "512GB");
        }

        // Restricted tablet colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        for (String storage : storageOptions) {
            int colorCount = Math.min(random.nextInt(3) + 2, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(30) + 8;
                variants.add(ProductVariant.builder()
                        .size(storage)
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + storage + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createLaptopVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // RAM/Storage configurations
        List<String> configurations;
        if ("Apple".equals(brand)) {
            configurations = Arrays.asList(
                "8GB/256GB", "8GB/512GB", "16GB/512GB", "16GB/1TB", "32GB/1TB", "32GB/2TB"
            );
        } else {
            configurations = Arrays.asList(
                "8GB/256GB", "16GB/512GB", "16GB/1TB", "32GB/512GB", "32GB/1TB"
            );
        }

        // Restricted laptop colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        for (String config : configurations) {
            int colorCount = Math.min(random.nextInt(3) + 1, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(20) + 3; // Lower stock for laptops
                variants.add(ProductVariant.builder()
                        .size(config) // Using size field for RAM/Storage config
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + config.replace("/", "-") + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createHandheldVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Storage options for gaming handhelds
        List<String> storageOptions = Arrays.asList("64GB", "256GB", "512GB", "1TB");

        // Restricted handheld colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        for (String storage : storageOptions) {
            int colorCount = Math.min(random.nextInt(2) + 1, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(15) + 2;
                variants.add(ProductVariant.builder()
                        .size(storage)
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + storage + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createKeyboardVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Switch types for mechanical keyboards
        List<String> switchTypes = Arrays.asList(
            "Red Switch", "Blue Switch", "Brown Switch", "Black Switch",
            "Silver Switch", "Tactile", "Linear", "Clicky"
        );

        // Restricted keyboard colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        // Some keyboards come in different switch types
        boolean hasSwitchVariants = product.getName().toLowerCase().contains("mechanical") ||
                                   Arrays.asList("Razer", "Corsair", "Logitech", "SteelSeries").contains(brand);

        if (hasSwitchVariants) {
            int switchCount = Math.min(random.nextInt(3) + 1, switchTypes.size());
            List<String> selectedSwitches = getRandomSubset(switchTypes, switchCount);

            for (String switchType : selectedSwitches) {
                int colorCount = Math.min(random.nextInt(2) + 1, colors.size());
                List<String> selectedColors = getRandomSubset(colors, colorCount);

                for (String color : selectedColors) {
                    int stock = random.nextInt(25) + 5;
                    variants.add(ProductVariant.builder()
                            .size(switchType) // Using size field for switch type
                            .color(color)
                            .stock(stock)
                            .sku(generateSKU(product.getName() + "-" + switchType.replace(" ", "") + "-" + color))
                            .product(product)
                            .build());
                }
            }
        } else {
            // Non-mechanical keyboards - just color variants
            int colorCount = Math.min(random.nextInt(3) + 1, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(30) + 10;
                variants.add(ProductVariant.builder()
                        .size("Standard")
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createMouseVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Restricted mouse colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        // Some gaming mice have different DPI variants
        boolean hasGamingVariants = product.getName().toLowerCase().contains("gaming") ||
                                   Arrays.asList("Razer", "Logitech", "Corsair", "SteelSeries").contains(brand);

        if (hasGamingVariants) {
            List<String> dpiVariants = Arrays.asList("Standard DPI", "High DPI", "Pro DPI");

            for (String dpi : dpiVariants) {
                int colorCount = Math.min(random.nextInt(2) + 1, colors.size());
                List<String> selectedColors = getRandomSubset(colors, colorCount);

                for (String color : selectedColors) {
                    int stock = random.nextInt(20) + 5;
                    variants.add(ProductVariant.builder()
                            .size(dpi)
                            .color(color)
                            .stock(stock)
                            .sku(generateSKU(product.getName() + "-" + dpi.replace(" ", "") + "-" + color))
                            .product(product)
                            .build());
                }
            }
        } else {
            // Regular mice - just color variants
            int colorCount = Math.min(random.nextInt(3) + 1, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(25) + 8;
                variants.add(ProductVariant.builder()
                        .size("Standard")
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createControllerVariants(Product product, String brand) {
        List<ProductVariant> variants = new ArrayList<>();

        // Restricted controller colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");

        // Special editions for some brands
        List<String> editions = Arrays.asList("Standard", "Elite", "Pro", "Limited Edition");

        for (String edition : editions.subList(0, random.nextInt(editions.size()) + 1)) {
            int colorCount = Math.min(random.nextInt(3) + 1, colors.size());
            List<String> selectedColors = getRandomSubset(colors, colorCount);

            for (String color : selectedColors) {
                int stock = random.nextInt(20) + 5;
                variants.add(ProductVariant.builder()
                        .size(edition)
                        .color(color)
                        .stock(stock)
                        .sku(generateSKU(product.getName() + "-" + edition.replace(" ", "") + "-" + color))
                        .product(product)
                        .build());
            }
        }

        return variants;
    }

    private List<ProductVariant> createGenericVariants(Product product) {
        List<ProductVariant> variants = new ArrayList<>();

        // Restricted generic colors
        List<String> colors = Arrays.asList("Red", "White", "Black", "Blue");
        int colorCount = Math.min(random.nextInt(3) + 1, colors.size());
        List<String> selectedColors = getRandomSubset(colors, colorCount);

        for (String color : selectedColors) {
            int stock = random.nextInt(30) + 10;
            variants.add(ProductVariant.builder()
                    .size("Standard")
                    .color(color)
                    .stock(stock)
                    .sku(generateSKU(product.getName() + "-" + color))
                    .product(product)
                    .build());
        }

        return variants;
    }

    /**
     * Gets a random subset of items from a list.
     */
    private <T> List<T> getRandomSubset(List<T> list, int size) {
        size = Math.min(size, list.size());
        List<T> copy = new ArrayList<>(list);
        Collections.shuffle(copy, random);
        return copy.subList(0, size);
    }

    /**
     * Generates a unique SKU for a product variant.
     */
    private String generateSKU(String productInfo) {
        // Remove special characters and spaces, then add random suffix
        String cleanInfo = productInfo.replaceAll("[^a-zA-Z0-9-]", "")
                .replaceAll("\\s+", "-")
                .toUpperCase();

        // Limit length and add random suffix for uniqueness
        if (cleanInfo.length() > 20) {
            cleanInfo = cleanInfo.substring(0, 20);
        }

        return cleanInfo + "-" + faker.number().numberBetween(1000, 9999);
    }

    private void seedDiscountCodes() {
        if (discountCodeRepo.count() > 0) return;
        System.out.println("Seeding discount codes...");
        List<DiscountCode> discountCodes = new ArrayList<>();

        for (int i = 0; i < NUM_DISCOUNT_CODES; i++) {
            DiscountCode discountCode = DiscountCode.builder()
                    .code(faker.lorem().word().toUpperCase() + faker.number().numberBetween(10, 99))
                    .discountAmount(faker.number().numberBetween(5, 25)) // Changed from discountPercentage to discountAmount
                    .expiryDate(LocalDate.now().plusDays(faker.number().numberBetween(30, 365))) // Changed to LocalDate
                    .isActive(random.nextBoolean())
                    .build();
            discountCodes.add(discountCode);
        }

        discountCodeRepo.saveAll(discountCodes);
        System.out.println(discountCodes.size() + " discount codes created.");
    }

    private void seedOrders() {
        if (orderRepo.count() > 0) return;
        System.out.println("Seeding orders...");
        List<User> users = userRepo.findAll();
        if (users.isEmpty()) return;

        List<Order> orders = new ArrayList<>();
        Order.Status[] statuses = Order.Status.values();

        for (int i = 0; i < NUM_ORDERS; i++) {
            User user = users.get(random.nextInt(users.size()));
            Order order = Order.builder()
                    .user(user)
                    .status(statuses[random.nextInt(statuses.length)])
                    .orderDate(randomCreatedAt(random))
                    .totalAmount(BigDecimal.ZERO) // Will be updated when order items are added
                    .build();
            orders.add(order);
        }

        orderRepo.saveAll(orders);
        System.out.println(orders.size() + " orders created.");
    }

    private void seedOrderItemsAndUpdateTotals() {
        if (orderItemRepo.count() > 0) return;
        System.out.println("Seeding order items...");
        List<Order> orders = orderRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();
        if (orders.isEmpty() || variants.isEmpty()) return;

        List<OrderItem> orderItems = new ArrayList<>();

        for (Order order : orders) {
            int itemCount = random.nextInt(3) + 1; // 1-3 items per order
            BigDecimal orderTotal = BigDecimal.ZERO;

            for (int i = 0; i < itemCount; i++) {
                ProductVariant variant = variants.get(random.nextInt(variants.size()));
                int quantity = random.nextInt(2) + 1; // 1-2 quantity
                BigDecimal itemPrice = variant.getProduct().getBasePrice();
                BigDecimal itemTotal = itemPrice.multiply(BigDecimal.valueOf(quantity));

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .productVariant(variant)
                        .quantity(quantity)
                        .unitPrice(itemPrice) // Changed from .price() to .unitPrice()
                        .build();
                orderItems.add(orderItem);
                orderTotal = orderTotal.add(itemTotal);
            }

            order.setTotalAmount(orderTotal);
        }

        orderItemRepo.saveAll(orderItems);
        orderRepo.saveAll(orders);
        System.out.println(orderItems.size() + " order items created and order totals updated.");
    }

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

    private String generateRandomTitle() {
        String[] places = {"City", "Downtown", "Uptown", "Riverside", "Mountain", "Beach", "Park", "Center"};
        String[] types = {"Flat", "Apartment", "House", "Office", "Studio", "Loft", "Building", "Complex"};
        return places[faker.number().numberBetween(0, places.length)] + " " +
                types[faker.number().numberBetween(0, types.length)];
    }

    private ShippingAddress.AddressType getRandomAddressType() {
        ShippingAddress.AddressType[] types = ShippingAddress.AddressType.values();
        return types[faker.number().numberBetween(0, types.length)];
    }

    private void seedCartItems() {
        if (cartItemRepo.count() > 0) return;
        System.out.println("Seeding cart items...");
        List<Cart> carts = cartRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();
        if (carts.isEmpty() || variants.isEmpty()) return;

        List<CartItem> cartItems = new ArrayList<>();

        for (Cart cart : carts) {
            // 50% chance a cart has items
            if (random.nextBoolean()) {
                int itemCount = random.nextInt(3) + 1; // 1-3 items per cart

                for (int i = 0; i < itemCount; i++) {
                    ProductVariant variant = variants.get(random.nextInt(variants.size()));
                    int quantity = random.nextInt(2) + 1; // 1-2 quantity

                    CartItem cartItem = CartItem.builder()
                            .cart(cart)
                            .productVariant(variant)
                            .quantity(quantity)
                            .build();
                    cartItems.add(cartItem);
                }
            }
        }

        cartItemRepo.saveAll(cartItems);
        System.out.println(cartItems.size() + " cart items created.");
    }

    private void seedProductReviews() {
        if (productReviewRepo.count() > 0) return;
        System.out.println("Seeding product reviews...");
        List<Product> products = productRepo.findAll();
        List<User> users = userRepo.findAll();
        if (products.isEmpty() || users.isEmpty()) return;

        List<ProductReview> reviews = new ArrayList<>();

        for (int i = 0; i < NUM_REVIEWS; i++) {
            Product product = products.get(random.nextInt(products.size()));
            User user = users.get(random.nextInt(users.size()));

            ProductReview review = ProductReview.builder()
                    .product(product)
                    .user(user)
                    .rating(random.nextInt(5) + 1) // 1-5 stars
                    .comment(faker.lorem().sentence(faker.number().numberBetween(5, 20)))
                    .createdAt(randomCreatedAt(random))
                    .build();
            reviews.add(review);
        }

        productReviewRepo.saveAll(reviews);
        System.out.println(reviews.size() + " product reviews created.");
    }

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
}
