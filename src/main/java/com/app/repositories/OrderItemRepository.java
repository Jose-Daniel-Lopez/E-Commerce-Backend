package com.app.repositories;

import com.app.entities.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    // Additional query methods can be defined here if needed
    // For example, to find OrderItems by Order ID:
    // List<OrderItem> findByOrderId(Long orderId);
}
