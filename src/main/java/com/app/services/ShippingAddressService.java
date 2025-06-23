package com.app.services;

import com.app.repositories.ShippingAddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingAddressService {

    // This service class will handle business logic related to ShippingAddress
    private final ShippingAddressRepository shippingAddressRepo;

    // Constructor injection for ShippingAddressRepository
    @Autowired
    public ShippingAddressService(ShippingAddressRepository shippingAddressRepo) {
        this.shippingAddressRepo = shippingAddressRepo;
    }
}
