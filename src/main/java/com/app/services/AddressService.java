package com.app.services;

import com.app.repositories.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class AddressService {

    // This service class will handle business logic related to Address
    private final AddressRepository addressRepo;

// Constructor injection for AddressRepository
    @Autowired
    public AddressService(AddressRepository addressRepo) {
        this.addressRepo = addressRepo;
    }
}
