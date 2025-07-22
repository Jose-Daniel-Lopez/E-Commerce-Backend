package com.app.repositories;

import com.app.entities.ShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShippingAddressRepository extends JpaRepository<ShippingAddress, Long> {

    // Find all addresses for a specific user
    List<ShippingAddress> findByUserId(Long userId);

    // Find addresses by user and address type
    List<ShippingAddress> findByUserIdAndAddressType(Long userId, ShippingAddress.AddressType addressType);

    // Check if user has any addresses
    boolean existsByUserId(Long userId);

    // Find default/primary address for user
    @Query("SELECT sa FROM ShippingAddress sa WHERE sa.user.id = :userId ORDER BY sa.id ASC")
    Optional<ShippingAddress> findFirstByUserId(@Param("userId") Long userId);

    // Count addresses per user
    long countByUserId(Long userId);
}
