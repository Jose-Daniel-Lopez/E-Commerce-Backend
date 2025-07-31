package com.app.entities;

import com.app.services.CartService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing a user's shopping cart.
 * <p>
 * A {@code Cart} holds a collection of {@link CartItem}s associated with a specific {@link User}.
 * It tracks when the cart was created and maintains a bidirectional relationship with its items
 * to ensure consistency in both directions (parent → children and child → parent).
 * </p>
 * <p>
 * This entity is mapped to the {@code carts} database table and supports:
 * </p>
 * <ul>
 *   <li>One-to-one association with {@link User}</li>
 *   <li>One-to-many composition with {@link CartItem}</li>
 *   <li>Validation of required fields and business rules (e.g., max 100 items)</li>
 * </ul>
 * <p>
 * The class uses Lombok annotations to reduce boilerplate code while maintaining immutability
 * and ease of construction via the builder pattern.
 * </p>
 *
 * @see CartItem
 * @see User
 * @see CartService
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "carts")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cart {

    /**
     * Unique identifier for the cart.
     * Generated automatically by the database using an identity strategy.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Timestamp indicating when the cart was created.
     * Cannot be null; required for audit and business logic (e.g., cart expiration).
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @NotNull(message = "Creation date is required")
    private LocalDateTime createdAt;

    /**
     * Reference to the user who owns this cart.
     * <p>
     * This is a one-to-one relationship, ensuring each user has at most one active cart.
     * The foreign key is stored in the {@code user_id} column of the {@code carts} table.
     * </p>
     * <p>
     * Lazy loading is used to avoid unnecessary data fetching when cart details are accessed
     * without needing full user info. JSON serialization excludes this field to prevent
     * circular references or excessive payload size.
     * </p>
     *
     * @see User
     */
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @NotNull(message = "User is required")
    private User user;

    /**
     * List of items currently in the cart.
     * <p>
     * This is the inverse (owning) side of the one-to-many relationship with {@link CartItem}.
     * The {@code mappedBy} attribute indicates that {@code CartItem.cart} owns the relationship.
     * </p>
     * <p>
     * All operations (persist, merge, remove) cascade from cart to items. Removed items are
     * automatically deleted from the database due to {@code orphanRemoval = true}.
     * </p>
     * <p>
     * Validation enforces a maximum of 100 unique items per cart to prevent abuse or performance issues.
     * </p>
     *
     * @see CartItem
     */
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Size(max = 100, message = "Cart cannot have more than 100 items")
    private List<CartItem> cartItems = new ArrayList<>();

    /**
     * Adds a {@link CartItem} to this cart and sets the bidirectional link.
     * <p>
     * This convenience method ensures that both sides of the relationship are synchronized:
     * the item references this cart, and this cart includes the item in its list.
     * </p>
     * <p>
     * It also initializes the list if it's {@code null}, though in practice it should never be
     * null due to initialization at declaration.
     * </p>
     *
     * @param cartItem the item to add; must not be {@code null}
     * @throws IllegalArgumentException if {@code cartItem} is {@code null}
     */
    public void addCartItem(CartItem cartItem) {
        if (cartItem == null) {
            throw new IllegalArgumentException("Cart item cannot be null");
        }
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        cartItems.add(cartItem);
        cartItem.setCart(this);
    }

    /**
     * Removes a {@link CartItem} from this cart and breaks the bidirectional link.
     * <p>
     * This method maintains referential integrity by clearing the {@code cart} reference
     * in the removed {@link CartItem}, which is necessary for proper JPA lifecycle management.
     * </p>
     *
     * @param cartItem the item to remove; ignored if {@code null} or not present
     */
    public void removeCartItem(CartItem cartItem) {
        if (cartItems != null && cartItem != null) {
            cartItems.remove(cartItem);
            cartItem.setCart(null);
        }
    }

    /**
     * Returns a string representation of the cart suitable for logging and debugging.
     * <p>
     * Includes key attributes: ID, creation timestamp, user ID (if available), and item count.
     * Avoids deep nesting or sensitive data exposure.
     * </p>
     *
     * @return a compact string summary of the cart
     */
    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", userId=" + (user != null ? user.getId() : null) +
                ", itemCount=" + (cartItems != null ? cartItems.size() : 0) +
                '}';
    }
}