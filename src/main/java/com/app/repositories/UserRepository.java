package com.app.repositories;

import com.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /* BUSINESS LOGIC RELATED TO USERS CAN BE HANDLED HERE */

    // Method to find a user by email
    Optional<User> findByEmail(String email);

    // Method to find a user by name
    Optional<User> findByUsername(String username);

    // Method to check if a user exists by email
    boolean existsByEmail(String email);

    // Method to check if a user exists by name
    boolean existsByUsername(String username);

}
