package com.app.data.seeder;

import com.app.entities.*;
import com.app.repositories.OrderItemRepository;
import com.app.repositories.OrderRepository;
import com.app.repositories.PaymentRepository;
import com.app.repositories.ProductVariantRepository;
import com.app.repositories.UserRepository;
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

public class OrderSeeder {

    private static final int NUM_ORDERS = 50;

    private final OrderRepository orderRepo;
    private final OrderItemRepository orderItemRepo;
    private final PaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final ProductVariantRepository productVariantRepo;
    private final Random random = new Random();

    public OrderSeeder(OrderRepository orderRepo, OrderItemRepository orderItemRepo, PaymentRepository paymentRepo, UserRepository userRepo, ProductVariantRepository productVariantRepo) {
        this.orderRepo = orderRepo;
        this.orderItemRepo = orderItemRepo;
        this.paymentRepo = paymentRepo;
        this.userRepo = userRepo;
        this.productVariantRepo = productVariantRepo;
    }

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

    private void seedOrders() {
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

    private LocalDateTime randomCreatedAt(Random random) {
        LocalDate startDate = LocalDate.now().minusDays(30);
        long startEpochDay = startDate.toEpochDay();
        long endEpochDay = LocalDate.now().toEpochDay();
        long randomDay = ThreadLocalRandom.current().longs(startEpochDay, endEpochDay + 1).findAny().orElse(startEpochDay);
        return LocalDateTime.of(LocalDate.ofEpochDay(randomDay), LocalTime.MIN)
                .plusMinutes(random.nextInt(1440));
    }
}

