package com.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "addresses")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Address name cannot be null")
    @Size(max = 100, message = "Address name cannot exceed 100 characters")
    private String name;

    @NotNull(message = "Street cannot be null")
    @Size(max = 100, message = "Street cannot exceed 255 characters")
    private String street;

    @NotNull(message = "City cannot be null")
    @Size(max = 50, message = "City cannot exceed 50 characters")
    private String city;

    @NotNull(message = "State cannot be null")
    @Size(max = 50, message = "State cannot exceed 50 characters")
    private String state;

    @NotNull(message = "Zip code cannot be null")
    @Pattern(regexp = "\\d{5}(-\\d{4})?", message = "Invalid zip code format")
    private String zipCode;

    @NotNull(message = "Country cannot be null")
    @Size(max = 50, message = "Country cannot exceed 50 characters")
    private String country;

    // Many-to-One relationship with User (bidirectional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    public Address(String name, String street, String city, String state, String zipCode, String country) {
        this.name = name;
        this.street = street;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
    }

    // custom toString method to avoid infinite recursion
    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", country='" + country + '\'' +
                ", userId=" + (user != null ? user.getId() : null) +
                '}';
    }
}
