package com.app.services;

import com.app.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderService {

    // This service class will handle business logic related to Order
    private final OrderRepository orderRepo;

    // Constructor injection for OrderRepository
    @Autowired
    public OrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }
}
