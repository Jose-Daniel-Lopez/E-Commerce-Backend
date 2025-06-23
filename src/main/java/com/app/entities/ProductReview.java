package com.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_reviews")
public class ProductReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;
    private String comment;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime createdAt;

    // Many-to-One relationship with User (bidirectional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonBackReference
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    // Constructor for creating a ProductReview with required fields
    public ProductReview(Integer rating, String comment, LocalDateTime createdAt) {
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "ProductReview{" +
                "id=" + id +
                ", rating=" + rating +
                ", comment='" + comment + '\'' +
                ", createdAt=" + createdAt +
                ", user=" + user +
                '}';
    }
}
