package com.app.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // Avoid infinite recursion in toString()
    @EqualsAndHashCode.Exclude // Avoid performance issues in equals/hashCode
    @Builder.Default // Ensure addresses is initialized
    private List<Address> addresses = new ArrayList<>();

    public enum Role {
        ADMIN,
        SELLER,
        CUSTOMER
    }

    // Convenience constructor for DataSeeder
    public User(Long id, String name, String email, String password, String avatar, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
        this.addresses = new ArrayList<>();
    }

    // Methods to manage addresses
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
}
