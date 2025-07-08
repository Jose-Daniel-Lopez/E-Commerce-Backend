package com.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(nullable = false)
    @NotNull(message = "Order date cannot be null")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @jakarta.validation.constraints.NotNull(message = "Order status cannot be null")
    private Status status;

    @Column(nullable = false)
    @DecimalMin(value = "0.0", inclusive = true, message = "Total amount must be at least 0")
    private BigDecimal totalAmount;

    // One-to-One relationship with Payment (bidirectional)
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    private Payment payment;

    // Many-to-One relationship with DiscountCode (bidirectional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_code_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DiscountCode discountCode;

    // One-to-Many relationship with OrderItem (bidirectional)
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<OrderItem> orderItems = new ArrayList<>();

    // Many-to-One relationship with User (bidirectional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    public enum Status {
        CREATED,
        PAID,
        SHIPPED,
        DELIVERED,
        CANCELED
    }

     // Property to check if this order has an applied discount code.
    @Transient
    @JsonProperty("hasDiscount")
    public boolean isHasDiscount() {
        return this.discountCode != null;
    }

    // Convenience method to set payment and maintain bidirectional relationship
    public void setPayment(Payment payment) {
        // If association is already established, do nothing to avoid loop
        if (this.payment == payment) {
            return;
        }

        // Break old link if it exists
        if (this.payment != null) {
            this.payment.setOrder(null);
        }

        this.payment = payment;

        // Establish new link if payment is not null
        if (payment != null) {
            payment.setOrder(this);
        }
    }

    // Convenience toString method to include payment and discount code details
    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderDate=" + orderDate +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", hasDiscount=" + isHasDiscount() +
                ", paymentId=" + (payment != null ? payment.getId() : null) +
                ", discountCode=" + (discountCode != null ? discountCode.getCode() : null) +
                '}';
    }
}
