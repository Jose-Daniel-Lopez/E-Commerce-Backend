package com.app.services;

import com.app.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderService {

    private final OrderRepository orderRepo;

    @Autowired
    public OrderService(OrderRepository orderRepo) {
        this.orderRepo = orderRepo;
    }
}
