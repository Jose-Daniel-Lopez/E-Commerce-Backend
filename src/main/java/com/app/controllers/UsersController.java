package com.app.controllers;

import com.app.entities.User;
import com.app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/users")
public class UsersController {

    /* --- This controller will handle HTTP requests related to User --- */
    /* --- You can add methods to handle various endpoints, e.g., GET, POST, PUT, DELETE --- */

    // Injecting the UserService to handle business logic
    private final UserService userService;

    // Constructor injection for UserService
    @Autowired
    public UsersController(UserService userService) {
        this.userService = userService;
    }

    // Method to get all users
     @GetMapping
     public List<User> getAllUsers() {
         return userService.getAllUsers();
     }
}
