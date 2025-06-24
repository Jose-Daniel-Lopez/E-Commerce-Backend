package com.app.services;

import com.app.entities.OrderItem;
import com.app.repositories.OrderItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderItemService {

    // Repository for OrderItem entity
    private final OrderItemRepository orderItemRepo;

    // Constructor-based dependency injection for OrderItemRepository
    @Autowired
    public OrderItemService(OrderItemRepository orderItemRepo) {
        this.orderItemRepo = orderItemRepo;
    }
}
