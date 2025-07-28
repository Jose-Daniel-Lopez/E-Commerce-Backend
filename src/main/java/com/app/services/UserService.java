package com.app.services;

import com.app.DTO.ChangePasswordDTO;
import com.app.DTO.UserPatchDTO;
import com.app.entities.User;
import com.app.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing business logic related to {@link User} entities.
 * <p>
 * This service handles user profile updates, password changes, and retrieval operations.
 * It ensures secure handling of sensitive data (e.g., passwords) and enforces validation rules.
 * </p>
 * <p>
 * Key responsibilities include:
 * </p>
 * <ul>
 *   <li>Patching user profile fields (username, location, avatar)</li>
 *   <li>Changing password with current password verification</li>
 *   <li>Retrieving users by ID</li>
 * </ul>
 * <p>
 * Password encoding is delegated to {@link PasswordEncoder} to ensure secure hashing.
 * Only non-sensitive fields are allowed to be updated via patching.
 * </p>
 */
@Service
public class UserService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new UserService with required dependencies.
     *
     * @param userRepo          the repository for user data operations; must not be null
     * @param passwordEncoder   the encoder for securely hashing passwords; must not be null
     */
    @Autowired
    public UserService(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    // === User Profile Management ===

    /**
     * Partially updates a user's profile using the provided patch data.
     * <p>
     * Only the fields present in the {@link UserPatchDTO} are updated.
     * Null fields are ignored. Password updates are not allowed here and must go through
     * {@link #changePassword(User, ChangePasswordDTO)}.
     * </p>
     *
     * @param userId     the ID of the user to update
     * @param patchDTO   the data transfer object containing optional fields to update
     * @return the updated {@link User} entity
     * @throws EntityNotFoundException if no user exists with the given ID
     */
    public User patchUser(Long userId, UserPatchDTO patchDTO) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        if (patchDTO.getUsername() != null) {
            user.setUsername(patchDTO.getUsername());
        }
        if (patchDTO.getLocation() != null) {
            user.setLocation(patchDTO.getLocation());
        }
        if (patchDTO.getAvatar() != null) {
            user.setAvatar(patchDTO.getAvatar());
        }

        return userRepo.save(user);
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id the user ID
     * @return the found {@link User} entity
     * @throws EntityNotFoundException if no user exists with the given ID
     */
    public User getUserById(Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    // === Password Management ===

    /**
     * Changes a user's password after validating the current password.
     * <p>
     * Enforces the following rules:
     * </p>
     * <ul>
     *   <li>Current password must match the stored one</li>
     *   <li>New password must not be blank</li>
     *   <li>New password must differ from the current one</li>
     * </ul>
     *
     * @param user               the user whose password is being changed; must not be null
     * @param changePasswordDTO  the DTO containing current and new passwords
     * @throws IllegalArgumentException if validation fails
     */
    public void changePassword(User user, ChangePasswordDTO changePasswordDTO) {
        // Validate current password
        if (!passwordEncoder.matches(changePasswordDTO.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        // Validate new password is not empty
        String newPassword = changePasswordDTO.getNewPassword();
        if (newPassword == null || newPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("New password cannot be empty");
        }

        // Prevent setting the same password
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new IllegalArgumentException("New password must be different from current password");
        }

        // Update and save
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }

    // === Future Method Suggestions ===
    //
    // public User createUser(UserRegistrationDTO dto) { ... }
    // public void deleteUser(Long id) { ... }
    // public Page<User> getAllUsers(Pageable pageable) { ... }
    // public User updateEmail(Long userId, String newEmail) { ... }
    // public boolean usernameExists(String username) { ... }
}