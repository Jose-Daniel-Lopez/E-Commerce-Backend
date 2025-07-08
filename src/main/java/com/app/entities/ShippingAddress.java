package com.app.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "shipping_addresses")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ShippingAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Street cannot be blank")
    private String street;

    @NotBlank(message = "City cannot be blank")
    private String city;

    @NotBlank(message = "State cannot be blank")
    private String state;

    @NotBlank(message = "Zip code cannot be blank")
    private String zipCode;

    @NotBlank(message = "Country cannot be blank")
    private String country;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
