package com.app.repositories;

import com.app.entities.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Long> {

    // Additional query methods can be defined here if needed
    // A helper method to find a code by its string representation
    Optional<DiscountCode> findByCode(String code);

}
