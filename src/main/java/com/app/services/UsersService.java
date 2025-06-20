package com.app.services;

import com.app.entities.Users;
import com.app.repositories.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsersService {

    // This service class will handle business logic related to Users
    private final UsersRepository userRepo;

    // Constructor injection for UsersRepository
    @Autowired
    public UsersService(UsersRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Add methods to interact with the UsersRepository as needed
    // For example, you can add methods to find users, save users, etc.
    // Example method to find a user by ID
    public Optional<Users> findUserById(Long id) {
        return userRepo.findById(id);
    }

    // Method to get all users
    public List<Users> getAllUsers() {
        return userRepo.findAll();
    }
}
