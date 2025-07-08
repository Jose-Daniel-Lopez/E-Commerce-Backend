package com.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payments")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @jakarta.validation.constraints.NotBlank(message = "Payment method cannot be blank")
    private String paymentMethod;

    @jakarta.validation.constraints.DecimalMin(value = "0.0", inclusive = true, message = "Amount must be at least 0")
    private Double amount;

    @Enumerated(EnumType.STRING)
    @jakarta.validation.constraints.NotNull(message = "Payment status cannot be null")
    private Status status;

    // One-to-One relationship with Order (bidirectional)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
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
