package com.app.services;

import com.app.entities.OrderItem;
import com.app.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing business logic related to {@link OrderItem} entities.
 * <p>
 * This service acts as an intermediary between the controller and data access layers,
 * providing a dedicated place to implement domain rules such as:
 * </p>
 * <ul>
 *   <li>Validating item quantities or prices before persistence</li>
 *   <li>Enriching order items with product details or tax information</li>
 *   <li>Handling adjustments or refunds at the item level</li>
 *   <li>Auditing changes to order items</li>
 * </ul>
 * <p>
 * Currently, this class holds a reference to {@link OrderItemRepository}
 * and is structured to support future expansion of these capabilities.
 * All data operations are delegated to the repository layer.
 * </p>
 */
@Service
public class OrderItemService {

    // Repository for performing CRUD operations on OrderItem entities
    private final OrderItemRepository orderItemRepo;

    /**
     * Constructs a new OrderItemService with the required repository dependency.
     *
     * @param orderItemRepo the repository used to persist and retrieve order items;
     *                      must not be null
     */
    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepo) {
        this.orderItemRepo = orderItemRepo;
    }

    // Suggested future methods:
    //
    // public OrderItem createOrderItem(OrderItem item) { ... }
    // public Optional<OrderItem> findById(Long id) { ... }
    // public List<OrderItem> findByOrderId(Long orderId) { ... }
    // public OrderItem updateQuantity(Long itemId, int newQuantity) { ... }
    // public void deleteById(Long itemId) { ... }
}