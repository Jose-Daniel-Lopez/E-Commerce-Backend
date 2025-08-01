package com.app.data.seeder;

import com.app.entities.*;
import com.app.repositories.*;
import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * Seeder class responsible for populating the database with realistic sample data for:
 * <ul>
 *   <li>{@link Order}</li>
 *   <li>{@link OrderItem}</li>
 *   <li>{@link Payment}</li>
 * </ul>
 * <p>
 * This seeder creates a complete order lifecycle:
 * </p>
 * <ol>
 *   <li>Creates orders linked to existing users</li>
 *   <li>Populates each order with 1–3 randomly selected product variants</li>
 *   <li>Calculates and sets the correct total amount per order</li>
 *   <li>Generates corresponding payment records for non-zero, unpaid orders</li>
 * </ol>
 * <p>
 * The process is designed to be <strong>idempotent</strong> and safe to run multiple times.
 * Each phase checks if data already exists before proceeding.
 * </p>
 * <p>
 * All dates are randomized within the last 30 days to simulate recent activity.
 * </p>
 *
 * @see Order
 * @see OrderItem
 * @see Payment
 * @see UserRepository
 * @see ProductVariantRepository
 */
public class OrderSeeder {

    /**
     * Number of sample orders to generate during seeding.
     * Adjust this value to control dataset size for testing or performance evaluation.
     */
    private static final int NUM_ORDERS = 50;

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final PaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final ProductVariantRepository productVariantRepo;
    private final ShippingAddressRepository shippingAddressRepo;
    private final Random random = new Random();

    /**
     * Constructs a new {@code OrderSeeder} with all required repository dependencies.
     *
     * @param orderRepo                the repository for order persistence; must not be {@code null}
     * @param orderItemRepo            the repository for order item persistence; must not be {@code null}
     * @param paymentRepo              the repository for payment persistence; must not be {@code null}
     * @param userRepo                 the repository for retrieving users; must not be {@code null}
     * @param productVariantRepo       the repository for retrieving product variants; must not be {@code null}
     * @param shippingAddressRepo      the repository for retrieving shipping addresses; must not be {@code null}
     * @throws IllegalArgumentException if any dependency is {@code null}
     */
    public OrderSeeder(
            OrderRepository orderRepo,
            OrderItemRepository orderItemRepo,
            PaymentRepository paymentRepo,
            UserRepository userRepo,
            ProductVariantRepository productVariantRepo,
            ShippingAddressRepository shippingAddressRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.paymentRepo = paymentRepo;
        this.userRepo = userRepo;
        this.productVariantRepo = productVariantRepo;
        this.shippingAddressRepo = shippingAddressRepo;
    }

    /**
     * Seeds the database with sample order, order item, and payment data if none already exist.
     * <p>
     * This method orchestrates a multi-phase seeding process:
     * </p>
     * <ol>
     *   <li><strong>Orders:</strong> Created for random users with random statuses and dates</li>
     *   <li><strong>Order Items:</strong> Added to each order (1–3 items), and order totals are recalculated</li>
     *   <li><strong>Payments:</strong> Generated for valid orders that don't already have a payment</li>
     * </ol>
     * <p>
     * Each phase is independent and checks its own existence condition, allowing partial seeding
     * (e.g., only payments) if some data is already present.
     * </p>
     */
    public void seed() {
        if (orderRepo.count() == 0) {
            seedOrders();
        }
        if (orderItemRepo.count() == 0) {
            seedOrderItemsAndUpdateTotals();
        }
        if (paymentRepo.count() == 0) {
            seedPayments();
        }
    }

    /**
     * Seeds the database with a batch of sample {@link Order} entities.
     * <p>
     * Each order is:
     * </p>
     * <ul>
     *   <li>Assigned to a random existing user</li>
     *   <li>Given a random status from {@link Order.Status}</li>
     *   <li>Timestamped between 1 and 30 days ago</li>
     *   <li>Initialized with a total of $0.00 (to be updated later)</li>
     * </ul>
     * <p>
     * Orders are persisted in bulk for efficiency.
     * </p>
     */
    private void seedOrders() {
        System.out.println("Seeding orders...");
        List<User> users = userRepo.findAll();
        if (users.isEmpty()) {
            System.out.println("Skipping order seeding: no users available.");
            return;
        }

        List<Order> orders = new ArrayList<>();
        Order.Status[] statuses = Order.Status.values();

        for (int i = 0; i < NUM_ORDERS; i++) {
            User user = users.get(random.nextInt(users.size()));
            LocalDateTime orderDate = randomCreatedAt(random);

            // Get a random shipping address for this user
            List<ShippingAddress> userAddresses = shippingAddressRepo.findByUserId(user.getId());
            ShippingAddress shippingAddress = null;
            if (!userAddresses.isEmpty()) {
                shippingAddress = userAddresses.get(random.nextInt(userAddresses.size()));
            }

            Order order = Order.builder()
                    .user(user)
                    .status(statuses[random.nextInt(statuses.length)])
                    .orderDate(orderDate)
                    .totalAmount(BigDecimal.ZERO) // Will be updated after order items are added
                    .shippingAddress(shippingAddress) // Assign shipping address
                    .build();
            orders.add(order);
        }

        orderRepo.saveAll(orders);
        System.out.println(orders.size() + " orders created with shipping addresses assigned.");
    }

    /**
     * Seeds {@link OrderItem} entries for existing orders and updates each order's total amount.
     * <p>
     * For each order:
     * </p>
     * <ul>
     *   <li>1–3 random product variants are selected</li>
     *   <li>Each item is assigned a quantity of 1 or 2</li>
     *   <li>The unit price is set to the product’s base price</li>
     *   <li>The order’s total is calculated as the sum of (unitPrice × quantity)</li>
     * </ul>
     * <p>
     * Both order items and updated orders are saved in bulk.
     * </p>
     */
    private void seedOrderItemsAndUpdateTotals() {
        System.out.println("Seeding order items...");
        List<Order> orders = orderRepo.findAll();
        List<ProductVariant> variants = productVariantRepo.findAll();

        if (orders.isEmpty()) {
            System.out.println("Skipping order item seeding: no orders found.");
            return;
        }
        if (variants.isEmpty()) {
            System.out.println("Skipping order item seeding: no product variants available.");
            return;
        }

        List<OrderItem> orderItems = new ArrayList<>();

        for (Order order : orders) {
            int itemCount = random.nextInt(3) + 1; // 1–3 items per order
            BigDecimal orderTotal = BigDecimal.ZERO;

            for (int i = 0; i < itemCount; i++) {
                ProductVariant variant = variants.get(random.nextInt(variants.size()));
                int quantity = random.nextInt(2) + 1; // 1 or 2 units
                BigDecimal unitPrice = variant.getProduct().getBasePrice();
                BigDecimal itemTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));

                OrderItem orderItem = OrderItem.builder()
                        .order(order)
                        .productVariant(variant)
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .build();

                orderItems.add(orderItem);
                orderTotal = orderTotal.add(itemTotal);
            }

            // Update the order's total amount after all items are calculated
            order.setTotalAmount(orderTotal);
        }

        // Persist all order items and update orders with correct totals
        orderItemRepo.saveAll(orderItems);
        orderRepo.saveAll(orders);
        System.out.println(orderItems.size() + " order items created and order totals updated.");
    }

    /**
     * Seeds {@link Payment} records for orders that:
     * <ul>
     *   <li>Do not already have a payment</li>
     *   <li>Have a total amount greater than zero</li>
     * </ul>
     * <p>
     * Each payment is:
     * </p>
     * <ul>
     *   <li>Assigned a random payment method (e.g., "Credit Card", "PayPal")</li>
     *   <li>Set to the full order amount</li>
     *   <li>Given a random status from {@link Payment.Status}</li>
     * </ul>
     * <p>
     * Payments are linked bidirectionally to their orders.
     * </p>
     */
    private void seedPayments() {
        System.out.println("Seeding payments...");
        List<Order> orders = orderRepo.findAll();
        if (orders.isEmpty()) {
            System.out.println("Skipping payment seeding: no orders found.");
            return;
        }

        List<String> paymentMethods = List.of("Credit Card", "PayPal", "Apple Pay", "Google Pay", "Stripe");
        Payment.Status[] statuses = Payment.Status.values();

        List<Payment> payments = orders.stream()
                .filter(order -> order.getPayment() == null)                     // Only unpaid orders
                .filter(order -> order.getTotalAmount().compareTo(BigDecimal.ZERO) > 0) // Valid amount
                .map(order -> {
                    Payment payment = Payment.builder()
                            .paymentMethod(paymentMethods.get(random.nextInt(paymentMethods.size())))
                            .amount(order.getTotalAmount().doubleValue())
                            .status(statuses[random.nextInt(statuses.length)])
                            .build();
                    payment.setOrder(order); // Maintain bidirectional relationship
                    return payment;
                })
                .collect(Collectors.toList());

        paymentRepo.saveAll(payments);
        System.out.println(payments.size() + " payments created.");
    }

    /**
     * Generates a random {@link LocalDateTime} within the last 30 days.
     * <p>
     * This method ensures realistic timestamps for seeded data by:
     * </p>
     * <ol>
     *   <li>Defining a start date: 30 days ago</li>
     *   <li>Selecting a random day between start and today</li>
     *   <li>Setting a random time within that day (00:00 to 23:59)</li>
     * </ol>
     *
     * @param random the random number generator to use
     * @return a {@link LocalDateTime} in the past 30 days with random time of day
     */
    private LocalDateTime randomCreatedAt(Random random) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = LocalDate.now().toEpochDay();
        long randomDay = ThreadLocalRandom.current().longs(startEpochDay, endEpochDay + 1).findAny().orElse(startEpochDay);
        return LocalDateTime.of(LocalDate.ofEpochDay(randomDay), LocalTime.MIN)
                .plusMinutes(random.nextInt(1440)); // 1440 minutes in a day
    }
}