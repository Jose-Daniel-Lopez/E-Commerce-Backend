package com.app.repositories;

import com.app.entities.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Additional query methods can be defined here if needed
    // For example, you can add methods to find payments by status or payment method
    // Example: List<Payment> findByStatus(Payment.Status status);
}
