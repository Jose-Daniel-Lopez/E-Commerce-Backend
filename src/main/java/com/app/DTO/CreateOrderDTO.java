package com.app.DTO;

import lombok.*;
import jakarta.validation.constraints.NotNull;

/**
 * Data Transfer Object for creating a new order.
 * <p>
 * This DTO encapsulates the necessary information to create an order from a user's cart,
 * including user identification, shipping address, and optional discount code.
 * </p>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderDTO {

    /**
     * ID of the user creating the order.
     * Must correspond to an existing user with an active cart.
     */
    @NotNull(message = "User ID is required")
    private Long userId;

    /**
     * ID of the shipping address for this order.
     * Must belong to the same user and exist in the system.
     */
    @NotNull(message = "Shipping address ID is required")
    private Long shippingAddressId;

    /**
     * Optional discount code to apply to the order.
     * If provided, must be a valid and applicable code.
     */
    private String discountCode;
}
