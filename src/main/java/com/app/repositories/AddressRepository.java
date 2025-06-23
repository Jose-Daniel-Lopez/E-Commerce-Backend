package com.app.repositories;

import com.app.entities.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

    // Additional methods related to Address can be defined here if needed
    // For example, you can add methods to find addresses by city, state, etc.
    // Example: List<Address> findByCity(String city);

}
