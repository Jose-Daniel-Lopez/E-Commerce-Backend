package com.app.controllers;

import com.app.entities.Order;
import com.app.entities.ShippingAddress;
import com.app.repositories.OrderRepository;
import com.app.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepo;

    @Autowired
    public OrderController(OrderService orderService, OrderRepository orderRepo) {
        this.orderService = orderService;
        this.orderRepo = orderRepo;
    }

    @GetMapping("/{id}/shippingAddress")
    public ResponseEntity<ShippingAddress> getShippingAddress(@PathVariable Long id) {
        try {
            Optional<ShippingAddress> address = orderService.getOrderShippingAddress(id);
            return address.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
