package com.app.hateoas;

import com.app.entities.Order;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderRepresentation extends RepresentationModel<OrderRepresentation> {

    private Long id;
    private LocalDateTime orderDate;
    private Order.Status status;
    private BigDecimal totalAmount;
    private boolean hasDiscount;

    public OrderRepresentation() {}

    public OrderRepresentation(Order order) {
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.status = order.getStatus();
        this.totalAmount = order.getTotalAmount();
        this.hasDiscount = order.isHasDiscount();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public Order.Status getStatus() { return status; }
    public void setStatus(Order.Status status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public boolean isHasDiscount() { return hasDiscount; }
    public void setHasDiscount(boolean hasDiscount) { this.hasDiscount = hasDiscount; }
}
