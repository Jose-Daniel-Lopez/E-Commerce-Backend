package com.app.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    private BigDecimal totalAmount;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    public enum Status {
        CREATED,
        PAID,
        SHIPPED,
        DELIVERED,
        CANCELED
    }

    // Convenience method to sync both sides of the relationship
    public void setPayment(Payment payment) {
        if (payment != null) {
            payment.setOrder(this);
        }
        this.payment = payment;
    }
}
