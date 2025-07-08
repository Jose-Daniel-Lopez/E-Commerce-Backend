package com.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "discount_codes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Code is required")
    @Size(min = 3, max = 30, message = "Code must be between 3 and 30 characters")
    private String code;

    @NotNull(message = "Discount percentage is required")
    @Min(value = 0, message = "Discount percentage must be at least 0")
    private Integer discountAmount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull(message = "Expiry date is required")
    private LocalDate expiryDate;

    private boolean isActive;

    // One-to-Many relationship with Order (bidirectional)
    @OneToMany(mappedBy = "discountCode")
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Order> orders;
}
