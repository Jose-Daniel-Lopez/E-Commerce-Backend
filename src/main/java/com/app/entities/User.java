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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // To manage the serialization of addresses without causing infinite recursion
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @Builder.Default
    private List<Address> addresses = new ArrayList<>();

    // Enumeration for user roles
    public enum Role {
        ADMIN,
        SELLER,
        CUSTOMER
    }


    public User(Long id, String name, String email, String password, String avatar, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
        this.addresses = new ArrayList<>();
    }

    // Convenience methods to manage addresses
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

    // custom toString method to avoid infinite recursion
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", addressCount=" + (addresses != null ? addresses.size() : 0) +
                '}';
    }
}
