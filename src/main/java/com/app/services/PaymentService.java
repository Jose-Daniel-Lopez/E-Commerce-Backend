package com.app.services;

import com.app.repositories.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class PaymentService {

    // This service class will handle business logic related to Payment
    private final PaymentRepository paymentRepo;

    // Constructor injection for PaymentRepository
    @Autowired
    public PaymentService(PaymentRepository paymentRepo) {
        this.paymentRepo = paymentRepo;
    }
}
