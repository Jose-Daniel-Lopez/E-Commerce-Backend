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

    // --- Configuration for Data Load ---
    private static final int NUM_USERS = 50;
    private static final int NUM_PRODUCTS_PER_CATEGORY = 15;
    private static final int NUM_ORDERS = 50;
    private static final int NUM_DISCOUNT_CODES = 10;
    private static final int NUM_REVIEWS = 70;

    // --- NEW: Structured Maps for Realistic Data Generation ---

    // Map to associate categories with a list of relevant brands.
    // This is the core change to ensure brands make sense for each product type.
    private static final Map<String, List<String>> CATEGORY_BRANDS = createCategoryBrandsMap();

    // Map for product name templates, which was already well-structured.
    private static final Map<String, List<String>> PRODUCT_NAMES = createProductNamesMap();

    private static Map<String, List<String>> createCategoryBrandsMap() {
        Map<String, List<String>> map = new HashMap<>();
        // Brands for Smartphones
        map.put("Smartphones", Arrays.asList("Apple", "Samsung", "Xiaomi", "Google", "Huawei", "OnePlus", "Sony", "Motorola", "Oppo", "Realme", "Asus", "Nokia"));
        // Brands for Computers
        map.put("Computers", Arrays.asList("Apple", "Dell", "HP", "Lenovo", "Microsoft", "Acer", "MSI", "Razer", "Asus", "Alienware"));
        // Brands for Tablets
        map.put("Tablets", Arrays.asList("Apple", "Samsung", "Microsoft", "Lenovo", "Xiaomi", "Huawei"));
        // Brands for Smartwatches
        map.put("Smartwatches", Arrays.asList("Apple", "Samsung", "Xiaomi", "Google", "Huawei", "OnePlus", "Sony", "Asus"));
        // Brands for Headphones
        map.put("Headphones", Arrays.asList("Apple", "Samsung", "Sony", "Logitech", "Corsair", "SteelSeries", "HyperX", "Razer"));
        // Brands for Peripherals
        List<String> peripheralBrands = Arrays.asList("Logitech", "Corsair", "Razer", "SteelSeries", "HyperX", "Asus", "Dell", "HP", "Lenovo", "Microsoft");
        map.put("Keyboards", peripheralBrands);
        map.put("Mice", peripheralBrands);
        // Brands for Cameras (limited to tech brands focusing on webcams/action cams)
        map.put("Cameras", Arrays.asList("Sony", "Logitech", "Razer"));
        // Brands for Audio
        map.put("Audio", Arrays.asList("Apple", "Google", "Sony", "LG", "Samsung", "Logitech", "Razer"));
        // For Gaming, the "brand" will be the platform owner
        map.put("Gaming", Arrays.asList("Sony", "Microsoft"));
        // Brands for Smart Home
        map.put("Smart Home", Arrays.asList("Google", "Apple", "Samsung", "Xiaomi", "LG", "TCL"));
        // For Accessories, we can allow a wider range of brands
        map.put("Accessories", Arrays.asList("Apple", "Samsung", "Xiaomi", "Google", "Huawei", "OnePlus", "Sony", "LG", "Motorola", "Oppo", "Vivo", "Realme", "Honor", "Nothing", "Asus", "Nokia", "TCL", "Fairphone", "RedMagic", "Dell", "HP", "Lenovo", "Microsoft", "Acer", "MSI", "Razer", "Logitech", "Corsair", "SteelSeries", "HyperX", "Alienware"));
        return map;
    }

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

    // --- Repositories and other dependencies ---
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

    public DataSeeder(UserRepository userRepo, OrderRepository orderRepo, DiscountCodeRepository discountCodeRepo, PaymentRepository paymentRepo, CartRepository cartRepo, CartItemRepository cartItemRepo, ProductReviewRepository productReviewRepo, ProductRepository productRepo, CategoryRepository categoryRepo, OrderItemRepository orderItemRepo, ProductVariantRepository productVariantRepo, ShippingAddressRepository shippingAddressRepo, WishlistRepository wishlistRepo, PasswordEncoder passwordEncoder) {
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

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Starting tech e-commerce data seeding process...");
        if (userRepo.count() == 0) seedUsers();
        if (cartRepo.count() == 0) seedCarts();
        if (categoryRepo.count() == 0) seedCategories();
        if (productRepo.count() == 0) seedProducts(); // This method is now improved
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

    // --- REFACTORED: seedProducts Method ---
    private void seedProducts() {
        if (productRepo.count() > 0) return;
        System.out.println("Seeding products with realistic names and attributes...");
        List<Category> categories = categoryRepo.findAll();
        if (categories.isEmpty()) {
            System.out.println("No categories found. Skipping product seeding.");
            return;
        }

        List<Product> products = new ArrayList<>();
        // Create a fallback list of all brands, just in case a category is not in our map
        Set<String> allBrandsSet = new HashSet<>();
        CATEGORY_BRANDS.values().forEach(allBrandsSet::addAll);
        List<String> allBrands = new ArrayList<>(allBrandsSet);

        for (Category category : categories) {
            String categoryName = category.getName();
            // Get brands and product names relevant to the category using our new maps
            List<String> validBrands = CATEGORY_BRANDS.getOrDefault(categoryName, allBrands);
            List<String> nameTemplates = PRODUCT_NAMES.getOrDefault(categoryName, Collections.singletonList("Product"));

            for (int i = 0; i < NUM_PRODUCTS_PER_CATEGORY; i++) {
                // Step 1: Select a relevant brand for the category
                String brand = validBrands.get(random.nextInt(validBrands.size()));
                String nameTemplate = nameTemplates.get(random.nextInt(nameTemplates.size()));

                // Step 2: Construct a meaningful product name.
                String productName = "Gaming".equals(categoryName) ? nameTemplate : brand + " " + nameTemplate;

                Product.ProductBuilder productBuilder = Product.builder()
                        .name(productName)
                        .brand(brand)
                        .description(faker.lorem().sentence(10))
                        .isFeatured(random.nextBoolean() && i < 3)
                        .basePrice(new BigDecimal(faker.commerce().price(50.00, 2500.00)
                                .replaceAll("[^\\d.]", "")).setScale(2, RoundingMode.HALF_UP))
                        .totalStock(0)
                        .category(category);

                // Step 3: Add category-specific attributes that are consistent with the brand
                switch (categoryName) {
                    case "Smartphones":
                        String cpu = switch (brand) {
                            case "Apple" -> faker.options().option("A16 Bionic", "A17 Pro");
                            case "Google" -> faker.options().option("Tensor G2", "Tensor G3");
                            case "Samsung" -> faker.options().option("Exynos 2400", "Snapdragon 8 Gen 3 for Galaxy");
                            case null, default -> faker.options().option("Snapdragon 8 Gen 3", "Dimensity 9300");
                        };
                        productBuilder.cpu(cpu)
                                .memory(faker.options().option("32GB","64GB","128GB", "256GB", "512GB", "1TB", "2TB", "4TB", "8TB"))
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
                        productBuilder.processorModel(processor).graphicsCard(gpu).operatingSystem(os)
                                .ramCapacity(new Integer[]{16, 32, 64}[random.nextInt(3)])
                                .storageCapacity(new Integer[]{512, 1024, 2048}[random.nextInt(3)]);
                        break;
                    case "Tablets":
                        String tabletOs;
                        if ("Apple".equals(brand)) {
                            tabletOs = "iPadOS";
                        } else if ("Microsoft".equals(brand)) {
                            tabletOs = "Windows 11";
                        } else {
                            tabletOs = "Android";
                        }
                        productBuilder.operatingSystem(tabletOs)
                                .screenSize(faker.options().option("10.2\"", "11\"", "12.9\""))
                                .storageCapacity(new Integer[]{128, 256, 512}[random.nextInt(3)])
                                .ramCapacity(new Integer[]{8, 12, 16}[random.nextInt(3)]);
                        break;
                    // Other cases from the original file can be added here.
                    // The ones below are less brand-dependent but still make sense.
                    case "Smartwatches":
                        productBuilder.compatibility("Apple".equals(brand) ? "iOS" : "Android");
                        break;
                    case "Gaming":
                        productBuilder.platform(faker.options().option("PlayStation 5", "Xbox Series X", "PC", "Nintendo Switch"));
                        break;
                    case "Accessories":
                        productBuilder.material(faker.commerce().material()).color(faker.color().name());
                        break;
                }
                products.add(productBuilder.build());
            }
        }
        productRepo.saveAll(products);
        System.out.println(products.size() + " realistic products created.");
    }

    private void seedWishlists() {
        if (wishlistRepo.count() > 0) return;
        System.out.println("Seeding wishlists...");
        List<User> users = userRepo.findAll();
        List<Product> products = productRepo.findAll();
        if (users.isEmpty() || products.isEmpty()) return;

        List<Wishlist> wishlists = new ArrayList<>();
        Random rand = new Random();

        for (User user : users) {
            int numWishlists = rand.nextInt(2) + 1; // 1-2 wishlists per user
            for (int i = 0; i < numWishlists; i++) {
                Wishlist wishlist = new Wishlist();
                wishlist.setTitle("Wishlist " + (i + 1) + " of " + user.getUsername());
                wishlist.setDescription("Auto-generated wishlist for " + user.getUsername());
                wishlist.setImageUrl("https://via.placeholder.com/150");
                wishlist.setProductUrl("https://example.com/product");
                wishlist.setPrice("Varies");
                wishlist.setCategory("Mixed");
                wishlist.setUser(user);

                // Add random products to wishlist
                Set<Product> wishlistProducts = new HashSet<>();
                int numProducts = rand.nextInt(5) + 1; // 1-5 products per wishlist
                for (int j = 0; j < numProducts; j++) {
                    wishlistProducts.add(products.get(rand.nextInt(products.size())));
                }
                wishlist.setProducts(wishlistProducts);

                wishlists.add(wishlist);
            }
        }
        wishlistRepo.saveAll(wishlists);
        System.out.println(wishlists.size() + " wishlists created.");
    }

    // --- Other Seeding Methods ---

    private void seedUsers() {
        if (userRepo.count() > 0) return;
        System.out.println("Seeding tech users...");
        List<User> users = new ArrayList<>();

        // Create some fixed users for testing purposes
        User admin = new User(null, "Tech Admin", "admin@techstore.com", passwordEncoder.encode("admin123"), "admin.png", User.Role.ADMIN);
        admin.setVerified(true);
        admin.addAddress(new Address(faker.name().title(), faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
        admin.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
        users.add(admin);

        User seller = new User(null, "Tech Seller", "seller@techstore.com", passwordEncoder.encode("seller123"), "seller.png", User.Role.SELLER);
        seller.addAddress(new Address(faker.name().title(), faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
        seller.setVerified(true);
        seller.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
        users.add(seller);

        // --- Hardcoded test user ---
        User testUser = new User(null, "test", "test@test.com", passwordEncoder.encode("123456"), "user.png", User.Role.CUSTOMER);
        testUser.setVerified(true);
        testUser.addAddress(new Address(faker.name().title(), faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
        testUser.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
        users.add(testUser);

        // Generate the rest of the users with tech-focused names
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
            // Add 1 to 2 addresses for each user
            int addressCount = random.nextInt(2) + 1;
            for (int j = 0; j < addressCount; j++) {
                user.addAddress(new Address(faker.name().title(), faker.address().streetAddress(), faker.address().city(), faker.address().state(), faker.address().zipCode(), "United States"));
            }
            user.setVerified(true); // Ensure all users are verified - This is for testing purposes
            user.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(365)));
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
                "Smartphones", "Smartwatches", "Cameras", "Headphones", "Computers",
                "Keyboards", "Mice", "Gaming", "Tablets", "Smart Home", "Audio", "Accessories"
        );
        List<String> categoryIcons = Arrays.asList(
                "gi-smartphone", "bi-smartwatch", "bi-camera", "la-headphones-solid",
                "bi-laptop", "bi-keyboard", "bi-mouse", "gi-console-controller",
                "co-tablet", "ri-home-wifi-line", "hi-music-note", "md-cable"
        );
        List<Category> categories = IntStream.range(0, categoryNames.size())
                .mapToObj(i -> Category.builder().name(categoryNames.get(i)).icon(categoryIcons.get(i)).build())
                .collect(Collectors.toList());
        categoryRepo.saveAll(categories);
        System.out.println(categories.size() + " categories created.");
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
        List<DiscountCode> codes = new ArrayList<>();
        for (int i = 0; i < NUM_DISCOUNT_CODES; i++) {
            codes.add(DiscountCode.builder()
                    .code(faker.commerce().promotionCode().toUpperCase())
                    .discountAmount(random.nextInt(50) + 5)
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
                    .totalAmount(BigDecimal.ZERO)
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

        List<ProductReview> reviews = new ArrayList<>();
        for (int i = 0; i < NUM_REVIEWS; i++) {
            reviews.add(ProductReview.builder()
                    .rating(random.nextInt(5) + 1)
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

