package com.app.services;

import com.app.DTO.CreateOrderDTO;
import com.app.entities.*;
import com.app.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class responsible for managing business logic related to {@code Order} entities.
 * <p>
 * This service serves as the central layer between the controller and repository,
 * designed to encapsulate domain rules such as:
 * </p>
 * <ul>
 *   <li>Order lifecycle management (e.g., pending, confirmed, shipped, canceled)</li>
 *   <li>Validation of order contents (e.g., inventory availability)</li>
 *   <li>Price calculation and tax/delivery fee application</li>
 *   <li>Integration with payment or shipping systems</li>
 *   <li>Generating order confirmations or receipts</li>
 * </ul>
 * <p>
 * Currently, this class holds a dependency on {@link OrderRepository}
 * and is structured to support future implementation of these features.
 * All data persistence and retrieval operations are delegated to the repository.
 * </p>
 */
@Service
public class OrderService {

    // Repository for data access operations related to Order entities
    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final CartRepository cartRepo;
    private final ShippingAddressRepository shippingAddressRepo;
    private final DiscountCodeRepository discountCodeRepo;

    /**
     * Constructs a new OrderService with the required repositories.
     *
     * @param orderRepo the repository used to interact with order data; must not be null
     * @param userRepo the repository for user data access
     * @param cartRepo the repository for cart data access
     * @param shippingAddressRepo the repository for shipping address data access
     * @param discountCodeRepo the repository for discount code data access
     */
    @Autowired
    public OrderService(OrderRepository orderRepo, UserRepository userRepo, CartRepository cartRepo,
                       ShippingAddressRepository shippingAddressRepo, DiscountCodeRepository discountCodeRepo) {
        this.orderRepo = orderRepo;
        this.userRepo = userRepo;
        this.cartRepo = cartRepo;
        this.shippingAddressRepo = shippingAddressRepo;
        this.discountCodeRepo = discountCodeRepo;
    }

    public Optional<ShippingAddress> getOrderShippingAddress(Long id) {
        // Fetch the order by ID from the repository
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        // Return the shipping address (which may be null) wrapped in Optional
        return Optional.ofNullable(order.getShippingAddress());
    }

    /**
     * Creates a new order from the user's cart.
     * <p>
     * This method performs the following operations:
     * </p>
     * <ul>
     *   <li>Validates user, cart, and shipping address existence</li>
     *   <li>Ensures the cart is not empty</li>
     *   <li>Validates shipping address ownership</li>
     *   <li>Applies discount code if provided and valid</li>
     *   <li>Converts cart items to order items</li>
     *   <li>Calculates total amount</li>
     *   <li>Clears the cart after successful order creation</li>
     * </ul>
     *
     * @param createOrderDTO the DTO containing order creation details
     * @return the created {@link Order}
     * @throws EntityNotFoundException if user, cart, or shipping address is not found
     * @throws IllegalArgumentException if cart is empty, shipping address doesn't belong to user, or discount code is invalid
     */
    @Transactional
    public Order createOrder(CreateOrderDTO createOrderDTO) {
        // Validate user exists
        User user = userRepo.findById(createOrderDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + createOrderDTO.getUserId()));

        // Find user's cart
        Cart cart = cartRepo.findByUserId(createOrderDTO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found for user ID: " + createOrderDTO.getUserId()));

        // Validate cart is not empty
        if (cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
            throw new IllegalArgumentException("Cannot create order from empty cart");
        }

        // Validate shipping address exists and belongs to user
        ShippingAddress shippingAddress = shippingAddressRepo.findById(createOrderDTO.getShippingAddressId())
                .orElseThrow(() -> new EntityNotFoundException("Shipping address not found with ID: " + createOrderDTO.getShippingAddressId()));

        if (!shippingAddress.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Shipping address does not belong to the specified user");
        }

        // Handle discount code if provided
        DiscountCode discountCode = null;
        if (createOrderDTO.getDiscountCode() != null && !createOrderDTO.getDiscountCode().trim().isEmpty()) {
            discountCode = discountCodeRepo.findByCode(createOrderDTO.getDiscountCode().trim())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid discount code: " + createOrderDTO.getDiscountCode()));

            // Additional validation can be added here for discount code eligibility
            // (expiry date, usage limits, minimum cart value, etc.)
        }

        // Create the order
        Order order = Order.builder()
                .user(user)
                .orderDate(LocalDateTime.now())
                .status(Order.Status.CREATED)
                .shippingAddress(shippingAddress)
                .discountCode(discountCode)
                .orderItems(new ArrayList<>())
                .build();

        // Convert cart items to order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : cart.getCartItems()) {
            // Get the current price from the product variant (assuming basePrice represents current price)
            BigDecimal unitPrice = cartItem.getProductVariant().getProduct().getBasePrice();
            if (unitPrice == null) {
                throw new IllegalStateException("Product variant does not have a valid price: " + cartItem.getProductVariant().getId());
            }

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariant(cartItem.getProductVariant())
                    .quantity(cartItem.getQuantity())
                    .unitPrice(unitPrice)
                    .build();

            order.getOrderItems().add(orderItem);

            // Calculate line total and add to order total
            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);
        }

        // Apply discount if applicable
        if (discountCode != null) {
            // This is a simplified discount application - you might want to implement more complex logic
            // based on your discount code entity structure (percentage vs fixed amount, minimum order value, etc.)
            BigDecimal discountAmount = calculateDiscountAmount(discountCode, totalAmount);
            totalAmount = totalAmount.subtract(discountAmount);

            // Ensure total doesn't go below zero
            if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                totalAmount = BigDecimal.ZERO;
            }
        }

        order.setTotalAmount(totalAmount);

        // Save the order
        Order savedOrder = orderRepo.save(order);

        // Clear the cart after successful order creation
        cart.getCartItems().clear();
        cartRepo.save(cart);

        return savedOrder;
    }

    /**
     * Calculates the discount amount to be applied to the order.
     * <p>
     * This is a simplified implementation. You should enhance this based on your
     * DiscountCode entity structure to handle percentage discounts, fixed amounts,
     * maximum discount limits, etc.
     * </p>
     *
     * @param discountCode the discount code to apply
     * @param orderTotal the total order amount before discount
     * @return the discount amount to subtract from the order total
     */
    private BigDecimal calculateDiscountAmount(DiscountCode discountCode, BigDecimal orderTotal) {
        // This is a placeholder implementation
        // You'll need to implement this based on your DiscountCode entity structure
        // For example, if your DiscountCode has fields like 'discountType', 'discountValue', etc.

        // Assuming a simple 10% discount for demonstration
        // Replace this with actual discount logic based on your DiscountCode entity
        return orderTotal.multiply(BigDecimal.valueOf(0.10));
    }

    // Suggested future methods:
    //
    // public Order findById(Long orderId) { ... }
    // public List<Order> findByUserId(Long userId) { ... }
    // public Order updateOrderStatus(Long orderId, OrderStatus status) { ... }
    // public void cancelOrder(Long orderId) { ... }
    // public List<Order> findByStatus(OrderStatus status) { ... }
}