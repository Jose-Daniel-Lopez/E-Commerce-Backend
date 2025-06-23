package com.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    // One-to-One relationship with Payment (bidirectional)
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    private Payment payment;

    // Many-to-One relationship with DiscountCode (bidirectional)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "discount_code_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DiscountCode discountCode;

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
