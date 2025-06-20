package com.app.repositories;

import com.app.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

    /* BUSINESS LOGIC RELATED TO USERS CAN BE HANDLED HERE */

    // Method to find a user by email
    Optional<Users> findByEmail(String email);

    // Method to find a user by name
    Optional<Users> findByName(String name);

    // Method to check if a user exists by email
    boolean existsByEmail(String email);

    // Method to check if a user exists by name
    boolean existsByName(String name);
}
