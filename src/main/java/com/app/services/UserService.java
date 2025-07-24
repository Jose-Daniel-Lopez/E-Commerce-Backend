package com.app.services;

import com.app.DTO.ChangePasswordDTO;
import com.app.DTO.UserPatchDTO;
import com.app.entities.User;
import com.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    // This service class will handle business logic related to User
    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    // Constructor injection for UserRepository
    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
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
        // Password changes are now handled exclusively through the changePassword method

        // Save the updated user back to the repository
        return userRepo.save(user);
    }

    public void changePassword(User user, ChangePasswordDTO changePasswordDTO) {
        // Validate current password
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password is not empty
        if (changePasswordDTO.getNewPassword() == null || changePasswordDTO.getNewPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        // Validate new password is different from current password
        if (passwordEncoder.matches(changePasswordDTO.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(changePasswordDTO.getNewPassword()));
        userRepo.save(user);
    }

    public User getUserById(Long id) {
        // Find the user by ID
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
}
