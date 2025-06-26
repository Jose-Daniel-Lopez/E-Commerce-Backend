package com.app.data;

import com.app.entities.*;
import com.app.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Component
public class DataSeeder implements CommandLineRunner {

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
    private final ShippingAddressRepository shippingAddressRepo; // Added for explicit seeding of shipping addresses
    // Note: AddressRepository is missing if Address is a separate entity

    private final PasswordEncoder passwordEncoder;
    private final Random random = new Random();

    // Constructor injection for repositories
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

    // This method is called when the application starts
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Seed entities in a specific order due to dependencies
        // Check if data already exists to prevent re-seeding on every startup

        // Users are fundamental and needed by many other entities
        if (userRepo.count() == 0) {
            seedUsers();
            // Assuming Address entity is managed within User (like in your original seedUsersWithCartsAndAddresses)
            // If Address is a standalone entity with its own repo, a separate seedAddresses method would be needed
            // and `seedUsers` would return the saved users to be used here.
        }

        // Carts are linked to Users
        if (cartRepo.count() == 0) {
            seedCarts();
        }

        // Categories must exist before products
        if (categoryRepo.count() == 0) {
            seedCategories();
        }

        // Products depend on Categories
        if (productRepo.count() == 0) {
            seedProducts();
        }

        // Product variants depend on Products
        if (productVariantRepo.count() == 0) {
            seedProductVariants();
        }

        // Discount codes are independent
        if (discountCodeRepo.count() == 0) {
            seedDiscountCodes();
        }

        // Orders depend on Users
        if (orderRepo.count() == 0) {
            seedOrders();
        }

        // Payments and Shipping Addresses depend on Orders.
        // These methods will retrieve existing orders to create associated data.
        if (paymentRepo.count() == 0) {
            seedPayments();
        }
        if (shippingAddressRepo.count() == 0) {
            seedShippingAddresses();
        }

        // Order items depend on Orders and Product Variants
        if (orderItemRepo.count() == 0) {
            seedOrderItems();
        }

        // Cart items depend on Carts and Product Variants
        if (cartItemRepo.count() == 0) {
            seedCartItems();
        }

        // Product reviews depend on Users and Products
        if (productReviewRepo.count() == 0) {
            seedProductReviews();
        }

        System.out.println("Data seeding completed successfully!");
    }

    /**
     * Seeds initial user data. This method also creates associated Address and Cart entities
     * because they are tightly coupled in the provided original code.
     * If Address and Cart were separate top-level entities, they would have their own seeding methods.
     */
    private void seedUsers() {
        if (userRepo.count() > 0) return; // Prevent re-seeding

        // Create users with associated addresses and carts
        User alice = new User(null, "Alicia Admin", "alicia@admin.com", passwordEncoder.encode("admin123"), "alicia.png", User.Role.ADMIN);
        // Addresses are added directly to the user as per original design
        alice.addAddress(new Address("Calle Mayor, 123", "Madrid", "Madrid", "28001", "España"));
        alice.addAddress(new Address("Avenida de la Paz, 45", "Barcelona", "Cataluña", "08001", "España"));

        User bob = new User(null, "Roberto Vendedor", "roberto@vendedor.com", passwordEncoder.encode("vendedor123"), "roberto.png", User.Role.SELLER);
        bob.addAddress(new Address("Plaza España, 7", "Sevilla", "Andalucía", "41001", "España"));

        User carol = new User(null, "Carla Cliente", "carla@cliente.com", passwordEncoder.encode("cliente123"), "carla.png", User.Role.CUSTOMER);
        carol.addAddress(new Address("Calle del Sol, 89", "Valencia", "Comunidad Valenciana", "46001", "España"));
        carol.addAddress(new Address("Paseo de Gracia, 1", "Barcelona", "Cataluña", "08007", "España"));

        User dave = new User(null, "David Vendedor", "david@vendedor.com", passwordEncoder.encode("davidpass"), "david.png", User.Role.SELLER);
        dave.addAddress(new Address("Calle Gran Vía, 10", "Madrid", "Madrid", "28013", "España"));

        User eve = new User(null, "Eva Cliente", "eva@cliente.com", passwordEncoder.encode("evapass"), "eva.png", User.Role.CUSTOMER);
        eve.addAddress(new Address("Rambla de Cataluña, 50", "Barcelona", "Cataluña", "08007", "España"));
        eve.addAddress(new Address("Calle Larios, 2", "Málaga", "Andalucía", "29005", "España"));

        userRepo.saveAll(List.of(alice, bob, carol, dave, eve));
        System.out.println("Usuarios, direcciones y carritos asociados creados.");
    }

    /**
     * Seeds initial shopping cart data for existing users.
     * Carts are created here as they were tightly coupled to users in the original `seedUsersWithCartsAndAddresses`.
     * This method retrieves users and assigns a new cart to any user that doesn't have one.
     */
    private void seedCarts() {
        if (cartRepo.count() > 0) return; // Prevent re-seeding

        List<User> users = userRepo.findAll();
        List<Cart> cartsToCreate = new ArrayList<>();

        for (User user : users) {
            // Ensure each user has a cart
            if (user.getCart() == null) {
                Cart cart = Cart.builder()
                        .user(user) // Link cart to user
                        .createdAt(LocalDateTime.now().minusDays(random.nextInt(30)))
                        .build();
                user.setCart(cart); // Set cart on user side for bidirectional relationship consistency
                cartsToCreate.add(cart);
            }
        }
        // Save carts (they will be saved via cascade if user is saved, but explicit save ensures it)
        cartRepo.saveAll(cartsToCreate);
        System.out.println("Carritos de muestra creados: " + cartsToCreate.size() + " carritos.");
    }

    /**
     * Seeds initial category data.
     */
    private void seedCategories() {
        if (categoryRepo.count() > 0) return; // Prevent re-seeding

        Category electronics = Category.builder().name("Electrónica").build();
        Category computers = Category.builder().name("Informática").build();
        Category audio = Category.builder().name("Audio").build();
        Category mobile = Category.builder().name("Dispositivos Móviles").build();
        Category home = Category.builder().name("Hogar").build();
        Category gaming = Category.builder().name("Gaming").build();

        categoryRepo.saveAll(List.of(electronics, computers, audio, mobile, home, gaming));
        System.out.println("Categorías de muestra creadas.");
    }

    /**
     * Seeds initial product data. Requires categories to be seeded first.
     */
    private void seedProducts() {
        if (productRepo.count() > 0) return; // Prevent re-seeding

        List<Category> allCategories = categoryRepo.findAll();
        // Map categories by name for easy access
        var categoriesMap = allCategories.stream()
                .collect(Collectors.toMap(Category::getName, category -> category));

        Product product1 = Product.builder()
                .name("Smartphone Pro X")
                .description("Última generación de smartphone con pantalla OLED y triple cámara.")
                .basePrice(new BigDecimal("699.99"))
                .totalStock(100)
                .category(categoriesMap.get("Electrónica"))
                .build();

        Product product2 = Product.builder()
                .name("Auriculares Inalámbricos Premium")
                .description("Auriculares con cancelación de ruido activa y sonido de alta fidelidad.")
                .basePrice(new BigDecimal("199.99"))
                .totalStock(250)
                .category(categoriesMap.get("Audio"))
                .build();

        Product product3 = Product.builder()
                .name("Portátil Ultra Delgado")
                .description("Portátil ultraligero con procesador de alto rendimiento y batería de larga duración.")
                .basePrice(new BigDecimal("1299.99"))
                .totalStock(50)
                .category(categoriesMap.get("Informática"))
                .build();

        Product product4 = Product.builder()
                .name("Ratón Gaming RGB")
                .description("Ratón de alta precisión con iluminación RGB personalizable y botones programables.")
                .basePrice(new BigDecimal("79.99"))
                .totalStock(150)
                .category(categoriesMap.get("Gaming"))
                .build();

        Product product5 = Product.builder()
                .name("Monitor Curvo 27 pulgadas")
                .description("Monitor Full HD curvo para una experiencia inmersiva, ideal para juegos y multimedia.")
                .basePrice(new BigDecimal("299.99"))
                .totalStock(75)
                .category(categoriesMap.get("Informática"))
                .build();

        productRepo.saveAll(List.of(product1, product2, product3, product4, product5));
        System.out.println("Productos de muestra creados con categorías asignadas.");
    }

    /**
     * Seeds initial product variant data. Requires products to be seeded first.
     */
    private void seedProductVariants() {
        if (productVariantRepo.count() > 0) return; // Prevent re-seeding

        List<Product> products = productRepo.findAll();
        if (products.isEmpty()) {
            System.out.println("No hay productos. Omitiendo la siembra de variantes de producto.");
            return;
        }

        List<ProductVariant> variants = new ArrayList<>();

        for (Product product : products) {
            switch (product.getName()) {
                case "Smartphone Pro X" -> {
                    variants.add(createVariant(product, "128GB", "Negro", 25, "SPX-128-NEG-001"));
                    variants.add(createVariant(product, "256GB", "Blanco", 20, "SPX-256-BLA-002"));
                    variants.add(createVariant(product, "512GB", "Azul", 15, "SPX-512-AZU-003"));
                }
                case "Auriculares Inalámbricos Premium" -> {
                    variants.add(createVariant(product, "Estándar", "Negro", 50, "AIP-STD-NEG-001"));
                    variants.add(createVariant(product, "Estándar", "Plata", 30, "AIP-STD-PLA-002"));
                }
                case "Portátil Ultra Delgado" -> {
                    variants.add(createVariant(product, "8GB/256GB SSD", "Gris Espacial", 10, "PUD-8256-GE-001"));
                    variants.add(createVariant(product, "16GB/512GB SSD", "Gris Espacial", 8, "PUD-16512-GE-002"));
                    variants.add(createVariant(product, "16GB/1TB SSD", "Plata", 5, "PUD-161TB-PLA-003"));
                }
                case "Ratón Gaming RGB" -> {
                    variants.add(createVariant(product, "Estándar", "Negro", 40, "RGR-STD-NEG-001"));
                    variants.add(createVariant(product, "RGB", "Multicolor", 35, "RGR-RGB-MUL-002"));
                }
                case "Monitor Curvo 27 pulgadas" -> {
                    variants.add(createVariant(product, "27 pulg.", "Negro", 75, "MC27-NEG-001"));
                }
                default -> {
                    variants.add(createVariant(product, "Estándar", "Defecto", 20,
                            generateSKU(product.getName(), "STD", "DEF")));
                }
            }
        }
        productVariantRepo.saveAll(variants);
        System.out.println("Variantes de producto de muestra creadas: " + variants.size() + " variantes.");
    }

    /**
     * Helper method to create a ProductVariant instance.
     */
    private ProductVariant createVariant(Product product, String size, String color, Integer stock, String sku) {
        return ProductVariant.builder()
                .size(size)
                .color(color)
                .stock(stock)
                .sku(sku)
                .product(product)
                .build();
    }

    /**
     * Helper method to generate a SKU for product variants.
     */
    private String generateSKU(String productName, String size, String color) {
        String prefix = productName.substring(0, Math.min(3, productName.length())).toUpperCase();
        String sizeCode = size.substring(0, Math.min(3, size.length())).toUpperCase();
        String colorCode = color.substring(0, Math.min(3, color.length())).toUpperCase();
        return String.format("%s-%s-%s-%03d", prefix, sizeCode, colorCode, random.nextInt(999) + 1);
    }

    /**
     * Seeds initial discount code data.
     */
    private void seedDiscountCodes() {
        if (discountCodeRepo.count() > 0) return; // Prevent re-seeding

        DiscountCode summer25 = DiscountCode.builder()
                .code("VERANO25")
                .discountAmount(25)
                .expiryDate(LocalDate.now().plusMonths(3))
                .isActive(true)
                .build();

        DiscountCode save10 = DiscountCode.builder()
                .code("AHORRA10")
                .discountAmount(10)
                .expiryDate(LocalDate.now().plusYears(1))
                .isActive(true)
                .build();

        DiscountCode expired = DiscountCode.builder()
                .code("INVIERNO_EXPIRADO")
                .discountAmount(10)
                .expiryDate(LocalDate.now().minusDays(1))
                .isActive(true)
                .build();

        DiscountCode inactive = DiscountCode.builder()
                .code("INACTIVO")
                .discountAmount(5)
                .expiryDate(LocalDate.now().plusYears(1))
                .isActive(false)
                .build();

        discountCodeRepo.saveAll(List.of(summer25, save10, expired, inactive));
        System.out.println("Códigos de descuento creados.");
    }

    /**
     * Seeds initial order data. Requires users to be seeded first.
     */
    private void seedOrders() {
        if (orderRepo.count() > 0) return; // Prevent re-seeding

        List<User> users = userRepo.findAll();
        if (users.isEmpty()) {
            System.out.println("No se encontraron usuarios. Omitiendo la siembra de órdenes.");
            return;
        }

        List<Order> ordersToCreate = new ArrayList<>();
        Order.Status[] orderStatuses = Order.Status.values();

        for (User user : users) {
            int orderCount = random.nextInt(2) + 1; // Each user gets 1-2 orders

            for (int i = 0; i < orderCount; i++) {
                Order order = Order.builder()
                        .orderDate(LocalDateTime.now().minusDays(random.nextInt(30)))
                        .status(orderStatuses[random.nextInt(orderStatuses.length)])
                        .totalAmount(BigDecimal.ZERO) // Will be calculated by OrderItems
                        .user(user)
                        .build();
                ordersToCreate.add(order);
            }
        }
        orderRepo.saveAll(ordersToCreate);
        System.out.println("Órdenes de muestra creadas: " + ordersToCreate.size() + " órdenes.");
    }

    /**
     * Seeds initial payment data. Requires orders to be seeded first.
     * Payments are created for each existing order.
     */
    private void seedPayments() {
        if (paymentRepo.count() > 0) return; // Prevent re-seeding

        List<Order> orders = orderRepo.findAll();
        if (orders.isEmpty()) {
            System.out.println("No se encontraron órdenes. Omitiendo la siembra de pagos.");
            return;
        }

        List<Payment> paymentsToCreate = new ArrayList<>();
        List<String> paymentMethods = List.of("Tarjeta de Crédito", "PayPal", "Transferencia Bancaria");
        Payment.Status[] paymentStatuses = Payment.Status.values();

        for (Order order : orders) {
            // Check if this order already has a payment to avoid duplicates if re-running
            if (order.getPayment() != null) continue;

            Payment payment = Payment.builder()
                    .paymentMethod(paymentMethods.get(random.nextInt(paymentMethods.size())))
                    .status(paymentStatuses[random.nextInt(paymentStatuses.length)])
                    .build();
            payment.setOrder(order); // Establish the bidirectional relationship
            paymentsToCreate.add(payment);
        }
        paymentRepo.saveAll(paymentsToCreate);
        System.out.println("Pagos de muestra creados: " + paymentsToCreate.size() + " pagos.");
    }

    /**
     * Seeds initial shipping address data. Requires orders to be seeded first.
     * A shipping address is created for each existing order.
     */
    private void seedShippingAddresses() {
        if (shippingAddressRepo.count() > 0) return; // Prevent re-seeding

        List<Order> orders = orderRepo.findAll();
        if (orders.isEmpty()) {
            System.out.println("No se encontraron órdenes. Omitiendo la siembra de direcciones de envío.");
            return;
        }

        List<ShippingAddress> shippingAddressesToCreate = new ArrayList<>();
        String[] streetNames = {"Calle Principal", "Avenida de los Álamos", "Ronda del Mar", "Paseo de la Montaña", "Plaza Mayor"};
        String[] cities = {"Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza", "Málaga", "Bilbao", "Alicante"};
        String[] provinces = {"Madrid", "Barcelona", "Valencia", "Sevilla", "Zaragoza", "Málaga", "Bizkaia", "Alicante"};
        String country = "España";

        for (Order order : orders) {
            // Check if this order already has a shipping address to avoid duplicates
            // This assumes a bidirectional relationship or a way to check if one exists.
            // If ShippingAddress is truly one-to-one with Order and unidirectional from ShippingAddress,
            // you might need to query shippingAddressRepo by order ID.
            // For simplicity, we assume an order won't have two shipping addresses in test data.
            // if (order.getShippingAddress() != null) continue; // If Order had a shippingAddress field

            String street = streetNames[random.nextInt(streetNames.length)] + ", " + (random.nextInt(99) + 1);
            String city = cities[random.nextInt(cities.length)];
            String province = provinces[random.nextInt(provinces.length)];
            String zipCode = String.format("%05d", random.nextInt(100000)); // Generates 5-digit zip code

            ShippingAddress address = ShippingAddress.builder()
                    .street(street)
                    .city(city)
                    .state(province) // Using 'state' for province/region
                    .zipCode(zipCode)
                    .country(country)
                    .order(order) // Associate the address with the order
                    .build();
            shippingAddressesToCreate.add(address);
        }
        shippingAddressRepo.saveAll(shippingAddressesToCreate);
        System.out.println("Direcciones de envío de muestra creadas: " + shippingAddressesToCreate.size() + " direcciones.");
    }

    /**
     * Seeds initial order item data. Requires orders and product variants to be seeded first.
     * Each order will have 2-4 items.
     */
    private void seedOrderItems() {
        if (orderItemRepo.count() > 0) return; // Prevent re-seeding

        List<Order> orders = orderRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();

        if (orders.isEmpty() || variants.isEmpty()) {
            System.out.println("No se encontraron órdenes o variantes de producto. Omitiendo la siembra de ítems de orden.");
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();

        for (Order order : orders) {
            int itemCount = random.nextInt(3) + 2; // 2 to 4 items per order

            for (int i = 0; i < itemCount; i++) {
                ProductVariant randomVariant = variants.get(random.nextInt(variants.size()));
                int quantity = random.nextInt(5) + 1; // 1 to 5 units of each item
                BigDecimal unitPrice = randomVariant.getProduct().getBasePrice();

                OrderItem orderItem = OrderItem.builder()
                        .quantity(quantity)
                        .unitPrice(unitPrice) // Use product's base price for simplicity or variant price if applicable
                        .order(order)
                        .productVariant(randomVariant)
                        .build();
                orderItems.add(orderItem);
            }
        }
        orderItemRepo.saveAll(orderItems);
        System.out.println("Ítems de orden de muestra creados: " + orderItems.size() + " ítems.");
    }

    /**
     * Seeds initial cart item data for existing carts. Requires carts and product variants to be seeded first.
     * Each cart will have 1-3 items.
     */
    private void seedCartItems() {
        if (cartItemRepo.count() > 0) return; // Prevent re-seeding

        List<Cart> carts = cartRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();

        if (carts.isEmpty() || variants.isEmpty()) {
            System.out.println("No se encontraron carritos o variantes de producto. Omitiendo la siembra de ítems de carrito.");
            return;
        }

        List<CartItem> cartItems = new ArrayList<>();

        for (Cart cart : carts) {
            int itemCount = random.nextInt(3) + 1; // 1 to 3 items per cart

            for (int i = 0; i < itemCount; i++) {
                ProductVariant randomVariant = variants.get(random.nextInt(variants.size()));
                int quantity = random.nextInt(3) + 1; // 1 to 3 units of each item

                CartItem cartItem = CartItem.builder()
                        .quantity(quantity)
                        .cart(cart)
                        .productVariant(randomVariant)
                        .build();
                cartItems.add(cartItem);
            }
        }
        cartItemRepo.saveAll(cartItems);
        System.out.println("Ítems de carrito de muestra creados: " + cartItems.size() + " ítems.");
    }

    /**
     * Seeds initial product review data. Requires users and products to be seeded first.
     */
    private void seedProductReviews() {
        if (productReviewRepo.count() > 0) return; // Prevent re-seeding

        List<User> users = userRepo.findAll();
        List<Product> products = productRepo.findAll();

        if (users.isEmpty() || products.isEmpty()) {
            System.out.println("No se encontraron usuarios o productos. Omitiendo la siembra de reseñas de productos.");
            return;
        }

        List<ProductReview> reviews = new ArrayList<>();

        for (User user : users) {
            int reviewCount = switch (user.getUsername()) {
                case "Alicia Admin" -> 2;
                case "Roberto Vendedor" -> 1;
                case "Carla Cliente" -> 3;
                default -> 1; // Default for others
            };

            for (int i = 1; i <= reviewCount; i++) {
                Product randomProduct = products.get(random.nextInt(products.size()));
                String commentText = "Reseña #" + i + " de " + user.getUsername() + " sobre " + randomProduct.getName() + ".";

                // Add some variety to comments
                if (random.nextBoolean()) {
                    commentText += " ¡Excelente producto, muy recomendado!";
                } else {
                    commentText += " Buen valor por el precio.";
                }

                ProductReview review = ProductReview.builder()
                        .rating(random.nextInt(5) + 1) // Rating from 1 to 5
                        .comment(commentText)
                        .createdAt(LocalDateTime.now().minusDays(random.nextInt(60))) // Reviews from last 60 days
                        .user(user)
                        .product(randomProduct)
                        .build();
                reviews.add(review);
            }
        }
        productReviewRepo.saveAll(reviews);
        System.out.println("Reseñas de productos creadas : " + reviews.size() + " reseñas.");
    }
}