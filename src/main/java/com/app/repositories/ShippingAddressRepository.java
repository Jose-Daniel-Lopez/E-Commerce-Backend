package com.app.repositories;

import com.app.entities.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    // This repository interface will handle CRUD operations for ShippingAddress
    // Additional custom query methods can be defined here if needed
}
