package com.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String paymentMethod;

    @Enumerated(EnumType.STRING)
    private Status status;

    // One-to-One relationship with Order (bidirectional)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    public enum Status {
        PENDING,
        COMPLETED,
        FAILED
    }

    // Convenience constructor for creating a Payment with just method and status
    public Payment(String paymentMethod, Status status) {
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    // Convenience method to sync both sides of the relationship
    public void setOrder(Order order) {
        if (order != null) {
            order.setPayment(this);
        }
        this.order = order;
    }
}
