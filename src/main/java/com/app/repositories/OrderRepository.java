package com.app.repositories;

import com.app.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long>{

    // Additional query methods can be defined here if needed
    // For example, you can add methods to find orders by status, date, etc.
    // Example: List<Order> findByStatus(Order.Status status); {
}
