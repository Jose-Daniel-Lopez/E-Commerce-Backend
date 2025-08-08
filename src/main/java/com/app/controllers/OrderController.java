package com.app.controllers;

import com.app.DTO.CreateOrderDTO;
import com.app.entities.Order;
import com.app.entities.ShippingAddress;
import com.app.repositories.OrderRepository;
import com.app.services.OrderService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    /**
     * Creates a new order from a user's cart.
     * <p>
     * This endpoint processes the checkout flow by:
     * </p>
     * <ul>
     *   <li>Converting cart items to order items</li>
     *   <li>Applying discount codes if provided</li>
     *   <li>Setting shipping address</li>
     *   <li>Calculating total amount</li>
     *   <li>Clearing the user's cart</li>
     * </ul>
     *
     * @param createOrderDTO the order creation data including user ID, shipping address, and optional discount code
     * @return ResponseEntity with the created Order and HTTP 201 status on success
     * @throws EntityNotFoundException if user, cart, or shipping address not found (returns 404)
     * @throws IllegalArgumentException if validation fails (returns 400)
     */
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderDTO createOrderDTO) {
        try {
            Order createdOrder = orderService.createOrder(createOrderDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Resource not found", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Invalid request", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Internal server error", "An unexpected error occurred"));
        }
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

    /**
     * Error response DTO for handling API errors.
     */
    public static class ErrorResponse {
        private String error;
        private String message;

        public ErrorResponse(String error, String message) {
            this.error = error;
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public String getMessage() {
            return message;
        }
    }
}
