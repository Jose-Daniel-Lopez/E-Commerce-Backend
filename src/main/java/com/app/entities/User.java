package com.app.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
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

    public enum Role {
        ADMIN,
        SELLER,
        CUSTOMER
    }

    public User() {
    }

    public User(Long id, String name, String email, String password, String avatar, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.role = role;
    }
}
