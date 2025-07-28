package com.app.controllers;

import com.app.DTO.RegisterDTO;
import com.app.entities.User;
import com.app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * REST controller for handling user registration and email verification.
 * <p>
 * Provides endpoints for:
 * <ul>
 *   <li>User registration with unique username/email checks</li>
 *   <li>Email verification via token</li>
 * </ul>
 * </p>
 * <p>
 * Endpoint: {@code /api/auth}
 * </p>
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173") // Allow frontend dev server
public class RegistrationController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructs a new RegistrationController with required dependencies.
     *
     * @param userRepo         repository for user persistence and queries
     * @param passwordEncoder  component to securely hash passwords
     */
    @Autowired
    public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user after validating username and email uniqueness.
     * <p>
     * On success, creates a new unverified user with a verification token.
     * Returns basic user info and the token (for dev/testing; in production,
     * this should be sent via email only).
     * </p>
     *
     * @param registerDTO the registration data (username, email, password, etc.)
     * @return {@link ResponseEntity} with success or error details
     * @response 200 User created successfully (unverified)
     * @response 409 Username or email already exists
     */
    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processRegistration(@RequestBody RegisterDTO registerDTO) {
        // Validate uniqueness of username
        if (userRepo.existsByUsername(registerDTO.getUsername())) {
            return errorResponse("Username already exists", HttpStatus.CONFLICT);
        }

        // Validate uniqueness of email
        if (userRepo.existsByEmail(registerDTO.getEmail())) {
            return errorResponse("Email already registered", HttpStatus.CONFLICT);
        }

        // Build and configure new user
        User newUser = User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .avatar(registerDTO.getAvatar())
                .role(registerDTO.getRole() != null ? registerDTO.getRole() : User.Role.CUSTOMER)
                .build();

        // Generate email verification token
        String verificationToken = UUID.randomUUID().toString();
        newUser.setVerificationToken(verificationToken);
        newUser.setVerified(false);

        // Persist user to database
        User savedUser = userRepo.save(newUser);

        // Return success response with minimal user info
        return ResponseEntity.ok(createSuccessResponse(savedUser));
    }

    /**
     * Verifies a user's email using a token sent during registration.
     * <p>
     * If the token is valid, marks the user as verified and removes the token.
     * </p>
     *
     * @param token the verification token sent in the email
     * @return {@link ResponseEntity} with success or error message
     * @response 200 Account verified successfully
     * @response 400 Invalid or expired token
     */
    @GetMapping("/verify")
    public ResponseEntity<?> verifyAccount(@RequestParam("token") String token) {
        User user = userRepo.findByVerificationToken(token);

        if (user == null) {
            return errorResponse("Invalid or expired verification token", HttpStatus.BAD_REQUEST);
        }

        // Mark user as verified and clear the token
        user.setVerified(true);
        user.setVerificationToken(null);
        userRepo.save(user);

        return ResponseEntity.ok(Collections.singletonMap("message", "Account verified successfully"));
    }

    // === Private Helper Methods ===

    /**
     * Creates a standardized error response.
     *
     * @param message the error message to include
     * @param status  the HTTP status to return
     * @return a {@link ResponseEntity} containing an error map
     */
    private ResponseEntity<Map<String, String>> errorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(Collections.singletonMap("error", message));
    }

    /**
     * Creates a standardized success response with user details.
     * <p>
     * Note: The {@code verificationToken} is included for testing convenience.
     * In a production system, this should NEVER be returned to the client.
     * It should only be sent via secure email.
     * </p>
     *
     * @param user the saved user entity
     * @return a map containing user info and success message
     */
    private Map<String, Object> createSuccessResponse(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole().name());
        userInfo.put("verificationToken", user.getVerificationToken()); // ⚠️ For dev only

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("user", userInfo);

        return response;
    }
}