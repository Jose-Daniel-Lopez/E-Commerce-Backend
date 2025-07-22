package com.app.repositories;

import com.app.entities.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{

    // Find orders that use a specific shipping address
    List<Order> findByShippingAddressId(Long shippingAddressId);

    // Find orders by user
    List<Order> findByUserId(Long userId);

    // Additional query methods can be defined here if needed
    // For example, you can add methods to find orders by status, date, etc.
    // Example: List<Order> findByStatus(Order.Status status); {
}
