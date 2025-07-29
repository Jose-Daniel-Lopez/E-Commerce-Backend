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

        rules.put("Sony", Arrays.asList(
                "Xperia %s", "Xperia %s Pro"
        ));

        rules.put("Xiaomi", Arrays.asList(
                "Mi %s", "Redmi %s", "Redmi Note %s", "Poco %s", "Xiaomi %s"
        ));

        rules.put("OnePlus", Arrays.asList(
                "OnePlus %s", "OnePlus %s Pro", "OnePlus Nord %s", "OnePlus %sT"
        ));

        rules.put("Microsoft", Arrays.asList(
                "Surface %s", "Surface Pro %s", "Surface Laptop %s", "Surface Book %s"
        ));

        rules.put("Dell", Arrays.asList(
                "XPS %s", "Inspiron %s", "Latitude %s", "Alienware %s", "G Series %s"
        ));

        rules.put("HP", Arrays.asList(
                "Spectre %s", "Envy %s", "Pavilion %s", "Omen %s", "EliteBook %s"
        ));

        rules.put("Lenovo", Arrays.asList(
                "ThinkPad %s", "Legion %s", "IdeaPad %s", "Yoga %s", "Flex %s"
        ));

        rules.put("ASUS", Arrays.asList(
                "ROG Zephyrus %s", "ROG Flow %s", "TUF Gaming %s", "Vivobook %s", "Zenbook %s"
        ));

        rules.put("Acer", Arrays.asList(
                "Predator %s", "Nitro %s", "Swift %s", "Aspire %s"
        ));

        rules.put("MSI", Arrays.asList(
                "GS %s", "GP %s", "Stealth %s", "Alpha %s"
        ));

        rules.put("Razer", Arrays.asList(
                "Blade %s", "Blade Stealth %s", "Blade Pro %s"
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
                "MX %s", "G %s", "Z %s", "PRO %s", "Craft %s", "POP %s"
        ));

        rules.put("Razer", Arrays.asList(
                "DeathAdder %s", "Viper %s", "Basilisk %s", "Naga %s",
                "BlackWidow %s", "Huntsman %s", "Ornata %s"
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

        rules.put("Microsoft", Arrays.asList(
                "Sculpt %s", "Ergonomic Keyboard %s", "Surface Mouse %s", "Arc Mouse %s"
        ));

        rules.put("Apple", Arrays.asList(
                "Magic Keyboard %s", "Magic Mouse %s"
        ));

        rules.put("8BitDo", Arrays.asList(
                "Ultimate %s", "Pro %s", "Zero %s", "SN30 %s"
        ));

        rules.put("Sony", Arrays.asList(
                "DualShock %s", "DualSense %s"
        ));

        rules.put("Microsoft", Arrays.asList(
                "Xbox Wireless Controller %s", "Elite %s", "Adaptive Controller"
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
        validCategories.put("Ducky", Arrays.asList("Keyboards"));
        validCategories.put("Keychron", Arrays.asList("Keyboards"));
        validCategories.put("Epomaker", Arrays.asList("Keyboards"));
        validCategories.put("8BitDo", Arrays.asList("Controllers"));
        validCategories.put("AYANEO", Arrays.asList("Handhelds"));
        validCategories.put("GPD", Arrays.asList("Handhelds"));
        validCategories.put("Steam", Arrays.asList("Handhelds"));

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
     * Injects realistic values into naming templates (e.g., model numbers, versions).
     */
    private String injectRealisticValues(String template, String brand, String category) {
        String result = template;
        while (result.contains("%s")) {
            String replacement = "";

            if (result.contains("iPhone") || result.contains("Galaxy S") || result.contains("Pixel") ||
                    result.contains("Xperia") || result.contains("OnePlus") || result.contains("Mi") ||
                    result.contains("Redmi") || result.contains("Poco")) {
                replacement = String.valueOf(faker.number().numberBetween(7, 25));
            } else if (result.contains("Surface") || result.contains("XPS") || result.contains("Spectre") ||
                    result.contains("ThinkPad") || result.contains("MacBook")) {
                replacement = String.valueOf(faker.number().numberBetween(3, 10));
            } else if (result.contains("Watch") || result.contains("Buds") || result.contains("AirPods")) {
                replacement = String.valueOf(faker.number().numberBetween(1, 8));
            } else if (result.contains("Alpha") || result.contains("EOS") || result.contains("Z ")) {
                replacement = String.valueOf(faker.number().numberBetween(5, 9)) + "000";
            } else if (result.contains("Blade") || result.contains("Razer")) {
                replacement = String.valueOf(faker.number().numberBetween(14, 18));
            } else if (result.contains("Xbox")) {
                replacement = faker.options().option("Series X", "Series S", "One X", "One S");
            } else {
                replacement = faker.options().option("X", "Pro", "Ultra", "Max", "Lite", "SE", "Edition", "Plus");
            }

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

    private String generateSKU(String productName) {
        String prefix = productName.substring(0, Math.min(3, productName.length())).toUpperCase();
        return String.format("%s-%04d-%03d", prefix, random.nextInt(10000), random.nextInt(1000));
    }

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
            if (random.nextDouble() < 0.7) {
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

    private List<Product> getRandomSubset(List<Product> list, int size) {
        size = Math.min(size, list.size());
        List<Product> copy = new ArrayList<>(list);
        Collections.shuffle(copy);
        return copy.subList(0, size);
    }
}

