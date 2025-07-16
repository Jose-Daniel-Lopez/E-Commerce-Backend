    package com.app.entities;

    import com.fasterxml.jackson.annotation.JsonFormat;
    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import com.fasterxml.jackson.annotation.JsonManagedReference;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.Email;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.NotNull;
    import lombok.*;
    import org.springframework.format.annotation.DateTimeFormat;
    import org.springframework.security.core.GrantedAuthority;
    import org.springframework.security.core.authority.SimpleGrantedAuthority;
    import org.springframework.security.core.userdetails.UserDetails;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.Collection;
    import java.util.Collections;
    import java.util.List;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Entity
    @Table(name = "users")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    public class User implements UserDetails {

        // ========== PRIMARY FIELDS ==========
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        @NotBlank(message = "Username cannot be blank")
        private String username;

        @Column(nullable = false, unique = true)
        @Email(message = "Email must be valid")
        @NotBlank(message = "Email cannot be blank")
        private String email;

        private String location;

        @Column(nullable = false)
        @NotBlank(message = "Password cannot be blank")
        private String password;

        private String avatar;

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private Role role;

        @Column(nullable = false)
        private boolean isVerified = false;

        @Column(unique = true)
        private String verificationToken;

        // ========== RELATIONSHIPS ==========
        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        @Builder.Default
        private List<Address> addresses = new ArrayList<>();

        @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
        @JsonManagedReference
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        private Cart cart;

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        @Builder.Default
        private List<ProductReview> productReviews = new ArrayList<>();

        @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonManagedReference
        @ToString.Exclude
        @EqualsAndHashCode.Exclude
        @Builder.Default
        private List<Order> orders = new ArrayList<>();

        // ========== ENUMS ==========
        public enum Role {
            CUSTOMER,
            SELLER,
            ADMIN
        }

        // ========== USERDETAILS IMPLEMENTATION ==========

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return Collections.singletonList(
                    new SimpleGrantedAuthority("ROLE_" + this.role.name())
            );
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }

        // ========== CONSTRUCTORS ==========
        public User(Long id, String username, String email, String password, String avatar, Role role) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.password = password;
            this.avatar = avatar;
            this.role = role;
            this.addresses = new ArrayList<>();
            this.productReviews = new ArrayList<>();
            this.orders = new ArrayList<>();
        }

        // ========== RELATIONSHIP MANAGEMENT METHODS ==========
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

        // ========== UTILITY METHODS ==========
        public String getDisplayName() {
            return this.username;
        }

        public boolean hasRole(Role role) {
            return this.role == role;
        }

        public boolean isAdmin() {
            return this.role == Role.ADMIN;
        }

        // ========== TO STRING ==========
        @Override
        public String toString() {
            return "User{" +
                    "id=" + id +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", role=" + role +
                    ", addressCount=" + (addresses != null ? addresses.size() : 0) +
                    ", hasCart=" + (cart != null) +
                    '}';
        }
    }
