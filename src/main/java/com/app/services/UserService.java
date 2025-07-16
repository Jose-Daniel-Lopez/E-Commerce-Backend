package com.app.services;

import com.app.DTO.UserPatchDTO;
import com.app.entities.User;
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

    public User patchUser(Long userId, UserPatchDTO patchDTO){
        // Find the user by ID
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Update the user's fields based on the patchDTO
        if (patchDTO.getUsername() != null) {
            user.setUsername(patchDTO.getUsername());
        }
        if (patchDTO.getLocation() != null) {
            user.setLocation(patchDTO.getLocation());
        }
        if (patchDTO.getAvatar() != null) {
            user.setAvatar(patchDTO.getAvatar());
        }

        // Save the updated user back to the repository
        return userRepo.save(user);
    }
}
