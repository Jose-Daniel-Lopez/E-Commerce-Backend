package com.app.services;

import com.app.entities.Order;
import com.app.entities.ShippingAddress;
import com.app.repositories.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * Constructs a new OrderService with the required repository.
     *
     * @param orderRepo the repository used to interact with order data; must not be null
     */
    @Autowired
    public OrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }

    public Optional<ShippingAddress> getOrderShippingAddress(Long id) {
        // Fetch the order by ID from the repository
        Order order = orderRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with id: " + id));

        // Return the shipping address (which may be null) wrapped in Optional
        return Optional.ofNullable(order.getShippingAddress());
    }

    // Suggested future methods:
    //
    // public Order createOrder(Cart cart, User user) { ... }
    // public Order findById(Long orderId) { ... }
    // public List<Order> findByUserId(Long userId) { ... }
    // public Order updateOrderStatus(Long orderId, OrderStatus status) { ... }
    // public void cancelOrder(Long orderId) { ... }
    // public List<Order> findByStatus(OrderStatus status) { ... }
}