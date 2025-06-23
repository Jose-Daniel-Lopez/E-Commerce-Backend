package com.app.services;

import com.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    // This service class will handle business logic related to User
    private final UserRepository userRepo;

    // Constructor injection for UserRepository
    @Autowired
    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // Add methods to interact with the UserRepository as needed
    // For example, you can add methods to find users, save users, etc.
}
