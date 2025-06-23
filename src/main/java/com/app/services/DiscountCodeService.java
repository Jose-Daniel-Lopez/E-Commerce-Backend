package com.app.services;

import com.app.repositories.DiscountCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DiscountCodeService {

    // This service class will handle business logic related to Discount Codes
    private final DiscountCodeRepository discountCodeRepo;

    // Constructor injection for DiscountCodeRepository
    @Autowired
    public DiscountCodeService(DiscountCodeRepository discountCodeRepo) {
        this.discountCodeRepo = discountCodeRepo;
    }
}
