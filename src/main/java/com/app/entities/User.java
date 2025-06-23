package com.app.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String avatar;

    @Enumerated(EnumType.STRING)
    private Role role;

    // One-to-Many relationship with Address (bidirectional)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    // One-to-One relationship with Cart (bidirectional)
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Cart cart;

    public enum Role {
        ADMIN,
        SELLER,
        CUSTOMER
    }

    // Constructor remains the same
    public User(Long id, String name, String email, String password, String avatar, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
        this.addresses = new ArrayList<>();
    }

    // Address management methods remain the same
    public void addAddress(Address address) {
        if (addresses == null) {
            addresses = new ArrayList<>();
        }
        addresses.add(address);
        address.setUser(this);
    }

    public void removeAddress(Address address) {
        if (addresses != null) {
            addresses.remove(address);
            address.setUser(null);
        }
    }

    // Convenience method to manage cart relationship
    public void setCart(Cart cart) {
        if (this.cart == cart) {
            return;
        }

        if (this.cart != null) {
            this.cart.setUser(null);
        }

        this.cart = cart;

        if (cart != null) {
            cart.setUser(this);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", addressCount=" + (addresses != null ? addresses.size() : 0) +
                ", hasCart=" + (cart != null) +
                '}';
    }
}

