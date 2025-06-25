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

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class RegistrationController {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationController(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> processRegistration(@RequestBody RegisterDTO registerDTO) {
        // Check for existing username or email
        if (userRepo.existsByUsername(registerDTO.getUsername())) {
            return errorResponse("Username already exists", HttpStatus.CONFLICT);
        }
        if (userRepo.existsByEmail(registerDTO.getEmail())) {
            return errorResponse("Email already registered", HttpStatus.CONFLICT);
        }

        // Build new user entity
        User newUser = User.builder()
                .username(registerDTO.getUsername())
                .email(registerDTO.getEmail())
                .password(passwordEncoder.encode(registerDTO.getPassword()))
                .avatar(registerDTO.getAvatar())
                .role(registerDTO.getRole() != null ? registerDTO.getRole() : User.Role.CUSTOMER)
                .build();

        User savedUser = userRepo.save(newUser);

        // Build success response
        return ResponseEntity.ok(createSuccessResponse(savedUser));
    }

    // Creates standardized error response
    private ResponseEntity<Map<String, String>> errorResponse(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(Collections.singletonMap("error", message));
    }

    // Creates standardized success response
    private Map<String, Object> createSuccessResponse(User user) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("id", user.getId());
        userInfo.put("username", user.getUsername());
        userInfo.put("email", user.getEmail());
        userInfo.put("role", user.getRole().toString());

        Map<String, Object> response = new HashMap<>();
        response.put("user", userInfo);
        response.put("message", "User registered successfully");

        return response;
    }
}